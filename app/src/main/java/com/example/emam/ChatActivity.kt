package com.example.emam

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

class ChatAdapter(private val messages: MutableList<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    class MessageViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val density = parent.context.resources.displayMetrics.density

        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        lp.topMargin    = (6 * density).toInt()
        lp.bottomMargin = (6 * density).toInt()

        val tv = TextView(parent.context)
        tv.layoutParams = lp
        tv.maxWidth     = (260 * density).toInt()
        tv.textSize     = 14f
        tv.setTextColor(Color.parseColor("#1A1A1A"))
        tv.setPadding(
            (14 * density).toInt(),
            (10 * density).toInt(),
            (14 * density).toInt(),
            (10 * density).toInt()
        )
        tv.setLineSpacing(3f, 1f)

        return MessageViewHolder(tv)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val msg     = messages[position]
        val density = holder.textView.context.resources.displayMetrics.density

        holder.textView.text = msg.text

        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        lp.topMargin    = (6 * density).toInt()
        lp.bottomMargin = (6 * density).toInt()

        if (msg.isUser) {
            lp.gravity     = Gravity.END
            lp.leftMargin  = (60 * density).toInt()
            lp.rightMargin = 0
            holder.textView.background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.WHITE)
                cornerRadius = 18f * density
                setStroke((1.5f * density).toInt(), Color.parseColor("#C62828"))
            }
        } else {
            lp.gravity     = Gravity.START
            lp.leftMargin  = 0
            lp.rightMargin = (60 * density).toInt()
            holder.textView.background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.parseColor("#FDECEA"))
                cornerRadius = 18f * density
                setStroke((1f * density).toInt(), Color.parseColor("#F5C6C6"))
            }
        }

        holder.textView.layoutParams = lp
    }

    override fun getItemCount(): Int = messages.size
}

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var inputMessage: EditText
    private lateinit var btnSend: Button
    private lateinit var inputBar: LinearLayout

    private val messages = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter

    private val client = OkHttpClient()
    private val apiKey = "YOUR API KEY"

    private val history = mutableListOf<Pair<String, String>>()

    private val maxHistoryPairs = 10

    @Suppress("SpellCheckingInspection")
    private val systemPrompt =
        "Kamu adalah MAX.AI, asisten nutrisi cerdas yang membantu pengguna " +
                "dengan rekomendasi makanan, informasi kalori, dan saran diet sehat. " +
                "Jawab dalam bahasa Indonesia, singkat dan ramah. " +
                "Gunakan format yang mudah dibaca dengan bullet point jika perlu."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recyclerView = findViewById(R.id.chatRecyclerView)
        inputMessage = findViewById(R.id.inputMessage)
        btnSend      = findViewById(R.id.btnSend)
        inputBar     = findViewById(R.id.inputBar)

        applyShapes()
        setupRecyclerView()
        setupNavigation()
        showWelcomeMessage()

        btnSend.setOnClickListener { sendMessage() }
    }

    private fun applyShapes() {
        val density = resources.displayMetrics.density

        inputBar.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.parseColor("#FFF5F5"))
            cornerRadius = 28f * density
            setStroke((1.5f * density).toInt(), Color.parseColor("#F5C6C6"))
        }

        btnSend.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor("#C62828"))
        }
    }

    private fun setupRecyclerView() {
        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        recyclerView.adapter = adapter
    }

    @Suppress("SpellCheckingInspection")
    private fun showWelcomeMessage() {
        addMessage(
            "Halo! Saya MAX.AI\n\nSaya siap membantu kamu dengan:\n" +
                    "- Rekomendasi makanan sehat\n" +
                    "- Informasi kalori & nutrisi\n" +
                    "- Saran diet sesuai anggaran\n\n" +
                    "Silakan tanya apa saja!",
            isUser = false
        )
    }

    private fun sendMessage() {
        val text = inputMessage.text.toString().trim()
        if (text.isEmpty()) return

        inputMessage.setText("")
        btnSend.isEnabled = false

        addMessage(text, isUser = true)
        showTypingIndicator()
        callGemini(text)
    }

    private fun addMessage(text: String, isUser: Boolean) {
        messages.add(ChatMessage(text, isUser))
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
    }

    private fun showTypingIndicator() {
        addMessage("...", isUser = false)
    }

    private fun removeTypingIndicator() {
        val idx = messages.indexOfLast { !it.isUser && it.text == "..." }
        if (idx != -1) {
            messages.removeAt(idx)
            adapter.notifyItemRemoved(idx)
        }
    }

    private fun callGemini(userMessage: String) {
        val url =
            "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=$apiKey"

        val contents = JSONArray()

        contents.put(buildTurn("user", systemPrompt))
        contents.put(buildTurn("model", "Baik, siap membantu!"))

        val trimmedHistory = if (history.size > maxHistoryPairs * 2) {
            history.takeLast(maxHistoryPairs * 2)
        } else {
            history
        }
        for (item in trimmedHistory) {
            contents.put(buildTurn(item.first, item.second))
        }

        contents.put(buildTurn("user", userMessage))

        val request = Request.Builder()
            .url(url)
            .post(
                JSONObject().put("contents", contents)
                    .toString()
                    .toRequestBody("application/json".toMediaType())
            )
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    removeTypingIndicator()
                    addMessage("Koneksi gagal, coba lagi.", isUser = false)
                    btnSend.isEnabled = true
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body?.string() ?: ""
                runOnUiThread {
                    removeTypingIndicator()
                    btnSend.isEnabled = true
                    try {
                        val replyText = JSONObject(res)
                            .optJSONArray("candidates")
                            ?.getJSONObject(0)
                            ?.optJSONObject("content")
                            ?.optJSONArray("parts")
                            ?.getJSONObject(0)
                            ?.optString("text", "") ?: ""

                        if (replyText.isNotEmpty()) {
                            history.add(Pair("user", userMessage))
                            history.add(Pair("model", replyText))
                            addMessage(replyText, isUser = false)
                        } else {
                            addMessage("Tidak ada jawaban, coba lagi.", isUser = false)
                        }
                    } catch (_: Exception) {
                        addMessage("Terjadi kesalahan, coba lagi.", isUser = false)
                    }
                }
            }
        })
    }

    private fun buildTurn(role: String, text: String): JSONObject =
        JSONObject().apply {
            put("role", role)
            put("parts", JSONArray().put(JSONObject().put("text", text)))
        }

    @Suppress("SpellCheckingInspection")
    private fun setupNavigation() {
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        findViewById<LinearLayout>(R.id.navScan).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        findViewById<LinearLayout>(R.id.navForum).setOnClickListener {
            Toast.makeText(this, "Forum coming soon!", Toast.LENGTH_SHORT).show()
        }
        findViewById<LinearLayout>(R.id.navUser).setOnClickListener {
            Toast.makeText(this, "Profile coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        client.dispatcher.cancelAll()
    }
}