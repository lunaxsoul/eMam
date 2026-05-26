package com.example.emam

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var historyThumbnail: ImageView
    private lateinit var imageFrame: FrameLayout
    private lateinit var resultText: TextView
    private lateinit var calorieBadge: TextView
    private lateinit var resultCard: CardView

    private val client = OkHttpClient()
    private val apiKey = "YOUR API KEY"

    // ── Updated color palette ─────────────────────────────────────────────────
    private val colorPrimary   = Color.parseColor("#AF3E4D")
    private val colorSecondary = Color.parseColor("#E4B1AB")
    private val colorAccent    = Color.parseColor("#EAF9D9")

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val path = result.data?.getStringExtra("photo_path")
                if (path != null) {
                    val bitmap = decodeSampledBitmap(path, 800, 800)
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap)
                        historyThumbnail.setImageBitmap(bitmap)
                        resultCard.visibility = View.GONE
                        calorieBadge.visibility = View.GONE
                        sendToGemini(bitmapToBase64(bitmap))
                    }
                }
            }
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) openInAppCamera()
            else Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView        = findViewById(R.id.imageView)
        historyThumbnail = findViewById(R.id.historyThumbnail)
        imageFrame       = findViewById(R.id.imageFrame)
        resultText       = findViewById(R.id.resultText)
        calorieBadge     = findViewById(R.id.calorieBadge)
        resultCard       = findViewById(R.id.resultCard)

        applyShapes()
        setupClipping()
        setupClickListeners()
    }

    private fun applyShapes() {
        val density = resources.displayMetrics.density

        // History pill — uses accent (green-ish)
        val btnCapture = findViewById<Button>(R.id.btnCapture)
        btnCapture.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(colorAccent)                              // fill hijau muda
            setStroke((3f * density).toInt(), colorPrimary)   // border primary
        }
        btnCapture.backgroundTintList = null

        historyThumbnail.background =
            GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(colorSecondary)
                setStroke((1f * density).toInt(), colorPrimary)
            }

        imageFrame.background =
            GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.WHITE)
                cornerRadius = 24f * density
                setStroke((3f * density).toInt(), colorPrimary)
            }

        imageView.background =
            GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.parseColor("#F5F5F5"))
                cornerRadius = 20f * density
            }

        // Capture button — primary with accent border
        findViewById<Button>(R.id.btnCapture).background =
            GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor("#AF3E4D"))
                setStroke((3f * density).toInt(), colorPrimary)
            }

        calorieBadge.background =
            GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.parseColor("#AA000000"))
                cornerRadius = 8f * density
            }
    }

    private fun setupClipping() {
        imageView.outlineProvider = ViewOutlineProvider.BACKGROUND
        imageView.clipToOutline = true
        historyThumbnail.outlineProvider = ViewOutlineProvider.BACKGROUND
        historyThumbnail.clipToOutline = true
    }

    private fun setupClickListeners() {
        findViewById<Button>(R.id.btnCapture).setOnClickListener {
            checkPermissionAndOpenCamera()
        }
        findViewById<LinearLayout>(R.id.btnHistory).setOnClickListener {
            Toast.makeText(this, "History coming soon!", Toast.LENGTH_SHORT).show()
        }
        // ── Navigation ─────────────────────────────────────────────────────────
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navChat).setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navForum).setOnClickListener {
            startActivity(Intent(this, ForumActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navUser).setOnClickListener {
            Toast.makeText(this, "Profile coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) openInAppCamera()
        else permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun openInAppCamera() {
        cameraLauncher.launch(Intent(this, CameraActivity::class.java))
    }

    private fun decodeSampledBitmap(path: String, reqWidth: Int, reqHeight: Int): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
                BitmapFactory.decodeFile(path, this)
                inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
                inJustDecodeBounds = false
            }
            BitmapFactory.decodeFile(path, options)
        } catch (_: Exception) { null }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        val (height, width) = options.outHeight to options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth  = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth)
                inSampleSize *= 2
        }
        return inSampleSize
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }

    private fun sendToGemini(base64Image: String) {
        runOnUiThread {
            resultText.text = "Menganalisis makananmu..."
            resultCard.visibility = View.VISIBLE
        }

        val url = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=$apiKey"

        val parts = JSONArray().apply {
            put(JSONObject().put(
                "text",
                "Analyze this food image and respond STRICTLY in this format:\n" +
                        "Food:\nCalories:\nProtein:\nCarbs:\nFat:"
            ))
            put(JSONObject().put("inline_data", JSONObject().apply {
                put("mime_type", "image/jpeg")
                put("data", base64Image)
            }))
        }

        val json = JSONObject().put("contents", JSONArray().put(JSONObject().put("parts", parts)))

        val request = Request.Builder()
            .url(url)
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    resultText.text = "Error: ${e.message}"
                    resultCard.visibility = View.VISIBLE
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body?.string() ?: ""
                runOnUiThread {
                    try {
                        val replyText = JSONObject(res)
                            .optJSONArray("candidates")
                            ?.getJSONObject(0)
                            ?.optJSONObject("content")
                            ?.optJSONArray("parts")
                            ?.getJSONObject(0)
                            ?.optString("text", "") ?: ""

                        if (replyText.isNotEmpty()) {
                            resultText.text = replyText
                            extractAndShowCalorieBadge(replyText)
                        } else {
                            resultText.text = "Tidak ada hasil, coba lagi"
                        }
                        resultCard.visibility = View.VISIBLE
                    } catch (_: Exception) {
                        resultText.text = "Parsing error"
                        resultCard.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun extractAndShowCalorieBadge(text: String) {
        val match = Regex("Calories:\\s*(\\d+[\\w\\s]*)", RegexOption.IGNORE_CASE).find(text)
        if (match != null) {
            calorieBadge.text = match.groupValues[1].trim()
            calorieBadge.visibility = View.VISIBLE
        }
    }
}
