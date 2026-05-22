package com.example.emam

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class Comment(
    val authorName: String,
    val text: String
)

data class ForumPost(
    val authorName: String,
    val date: String,
    val content: String,
    var likes: Int = 0,
    var bookmarks: Int = 0,
    var isLiked: Boolean = false,
    var isBookmarked: Boolean = false,
    val commentList: MutableList<Comment> = mutableListOf()
)

class ForumAdapter(
    private val posts: MutableList<ForumPost>,
    private val onCommentClick: (ForumPost, Int) -> Unit
) : RecyclerView.Adapter<ForumAdapter.PostViewHolder>() {

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val authorName: TextView      = view.findViewById(R.id.postAuthorName)
        val postDate: TextView        = view.findViewById(R.id.postDate)
        val postContent: TextView     = view.findViewById(R.id.postContent)
        val btnLike: LinearLayout     = view.findViewById(R.id.btnLike)
        val btnComment: LinearLayout  = view.findViewById(R.id.btnComment)
        val btnBookmark: LinearLayout = view.findViewById(R.id.btnBookmark)
        val likeCount: TextView       = view.findViewById(R.id.likeCount)
        val commentCount: TextView    = view.findViewById(R.id.commentCount)
        val bookmarkCount: TextView   = view.findViewById(R.id.bookmarkCount)
        val likeIcon: TextView        = view.findViewById(R.id.likeIcon)
        val bookmarkIcon: TextView    = view.findViewById(R.id.bookmarkIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forum_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        holder.authorName.text    = post.authorName
        holder.postDate.text      = post.date
        holder.postContent.text   = post.content
        holder.likeCount.text     = post.likes.toString()
        holder.commentCount.text  = post.commentList.size.toString()
        holder.bookmarkCount.text = post.bookmarks.toString()

        // Like icon state
        holder.likeIcon.text = if (post.isLiked) "♥" else "♡"
        holder.likeIcon.setTextColor(
            if (post.isLiked) Color.parseColor("#C62828") else Color.parseColor("#9E9E9E")
        )

        // Bookmark icon state
        holder.bookmarkIcon.text = if (post.isBookmarked) "🔖" else "🏷"

        // Like toggle
        holder.btnLike.setOnClickListener {
            val p = posts[holder.adapterPosition]
            p.isLiked = !p.isLiked
            p.likes   = if (p.isLiked) p.likes + 1 else p.likes - 1
            notifyItemChanged(holder.adapterPosition)
        }

        // Comment — open dialog
        holder.btnComment.setOnClickListener {
            onCommentClick(post, holder.adapterPosition)
        }

        // Bookmark toggle
        holder.btnBookmark.setOnClickListener {
            val p = posts[holder.adapterPosition]
            p.isBookmarked = !p.isBookmarked
            p.bookmarks    = if (p.isBookmarked) p.bookmarks + 1 else p.bookmarks - 1
            notifyItemChanged(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int = posts.size
}

class ForumActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var inputPost: EditText
    private lateinit var btnPost: Button
    private lateinit var postInputBar: LinearLayout

    private val posts = mutableListOf<ForumPost>()
    private lateinit var adapter: ForumAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)

        recyclerView = findViewById(R.id.postsRecyclerView)
        inputPost    = findViewById(R.id.inputPost)
        btnPost      = findViewById(R.id.btnPost)
        postInputBar = findViewById(R.id.postInputBar)

        applyShapes()
        loadSamplePosts()
        setupRecyclerView()
        setupNavigation()

        btnPost.setOnClickListener { submitPost() }
    }

    private fun applyShapes() {
        val density = resources.displayMetrics.density

        postInputBar.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.WHITE)
            cornerRadius = 12f * density
            setStroke((1f * density).toInt(), Color.parseColor("#E0E0E0"))
        }

        btnPost.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.parseColor("#AF3E4D"))
            cornerRadius = 8f * density
        }
        btnPost.backgroundTintList = null
    }

    // ── Sample posts ──────────────────────────────────────────────────────────

    private fun loadSamplePosts() {
        posts.add(ForumPost(
            authorName = "Misca Alexandra",
            date       = "20 September 2025",
            content    = "Halo bunda-bunda!\n\nAku mau sharing sedikit menu yang sering aku makan supaya kebutuhan nutrisi tetap terpenuhi selama hamil.\n\nBeberapa makanan yang rutin aku konsumsi:\n🥚 Telur rebus – sumber protein dan vitamin D\n🥦 Bayam dan brokoli – kaya zat besi dan folat\n🐟 Ikan kembung atau ikan salmon – tinggi omega-3\n🥛 Susu ibu hamil – membantu memenuhi kebutuhan kalsium\n\nSemoga membantu ya bunda 💛",
            likes      = 14,
            bookmarks  = 90,
            commentList = mutableListOf(
                Comment("Sari Dewi", "Makasih sharingnya bunda! Aku juga suka makan telur rebus 😊"),
                Comment("Rina P.", "Wah lengkap banget! Boleh tau resep lengkapnya bunda?")
            )
        ))
        posts.add(ForumPost(
            authorName = "Sari Dewi",
            date       = "18 September 2025",
            content    = "Bunda-bunda ada yang punya tips buat atasi mual di trimester pertama?\n\nAku udah coba jahe hangat tapi masih sering mual pas pagi hari 😢\n\nMohon sharingnya ya!",
            likes      = 23,
            bookmarks  = 45,
            commentList = mutableListOf(
                Comment("Misca A.", "Coba makan biskuit kering sebelum bangun dari tempat tidur bunda, biasanya membantu!"),
                Comment("Dewi R.", "Aku dulu pakai aromaterapi lemon, lumayan membantu mengurangi mual 🍋")
            )
        ))
        posts.add(ForumPost(
            authorName = "Rina Pratiwi",
            date       = "15 September 2025",
            content    = "Alhamdulillah minggu ini hasil USG bagus! 🎉\n\nDokter menyarankan untuk tetap jaga asupan zat besi karena kadar HB masih sedikit rendah. Ada yang punya rekomendasi makanan tinggi zat besi yang enak?",
            likes      = 31,
            bookmarks  = 67,
            commentList = mutableListOf(
                Comment("Sari D.", "Hati ayam bunda! Tinggi zat besi dan enak ditumis 😋"),
                Comment("Anisa W.", "Kacang merah juga bagus bunda, bisa dibuat sup 🫘"),
                Comment("Misca A.", "Selamat ya bunda! Daging merah juga bisa jadi pilihan 🥩")
            )
        ))
        posts.add(ForumPost(
            authorName = "Dewi Rahayu",
            date       = "12 September 2025",
            content    = "Tips olahraga ringan untuk ibu hamil trimester 2:\n\n🚶 Jalan kaki 30 menit setiap pagi\n🧘 Prenatal yoga 2x seminggu\n🏊 Renang – sangat direkomendasikan dokterku\n\nPenting: selalu konsultasi dengan dokter sebelum mulai olahraga ya bunda!",
            likes      = 45,
            bookmarks  = 120,
            commentList = mutableListOf(
                Comment("Rina P.", "Makasih tipsnya bunda! Aku langsung coba jalan kaki pagi 🌅")
            )
        ))
        posts.add(ForumPost(
            authorName = "Anisa Wahyu",
            date       = "10 September 2025",
            content    = "Rekomendasi suplemen untuk ibu hamil yang dokterku sarankan:\n\n💊 Asam folat\n💊 Vitamin D3\n💊 Zat besi\n💊 Kalsium\n\nTapi tetap konsultasi dengan dokter kandungan masing-masing ya bunda, kebutuhan setiap orang bisa berbeda!",
            likes      = 67,
            bookmarks  = 200,
            commentList = mutableListOf()
        ))
    }

    private fun setupRecyclerView() {
        adapter = ForumAdapter(posts) { post, position ->
            showCommentDialog(post, position)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = true
    }

    // ── Comment Dialog ────────────────────────────────────────────────────────

    private fun showCommentDialog(post: ForumPost, postPosition: Int) {
        val density = resources.displayMetrics.density

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                (20 * density).toInt(), (16 * density).toInt(),
                (20 * density).toInt(), (16 * density).toInt()
            )
        }

        if (post.commentList.isNotEmpty()) {
            val commentsLabel = TextView(this).apply {
                text = "${post.commentList.size} Komentar"
                textSize = 13f
                setTextColor(Color.parseColor("#9E9E9E"))
                setPadding(0, 0, 0, (8 * density).toInt())
            }
            rootLayout.addView(commentsLabel)

            val scrollView = ScrollView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (200 * density).toInt()
                )
            }
            val commentContainer = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
            }

            for (comment in post.commentList) {
                val commentView = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(0, (8 * density).toInt(), 0, (8 * density).toInt())
                }
                val authorView = TextView(this).apply {
                    text = comment.authorName
                    textSize = 13f
                    setTextColor(Color.parseColor("#C62828"))
                    setTypeface(null, android.graphics.Typeface.BOLD)
                }
                val textView = TextView(this).apply {
                    text = comment.text
                    textSize = 13f
                    setTextColor(Color.parseColor("#1A1A1A"))
                    setPadding(0, (2 * density).toInt(), 0, 0)
                }
                val divider = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1
                    ).also { it.topMargin = (8 * density).toInt() }
                    setBackgroundColor(Color.parseColor("#F0F0F0"))
                }

                commentView.addView(authorView)
                commentView.addView(textView)
                commentView.addView(divider)
                commentContainer.addView(commentView)
            }
            scrollView.addView(commentContainer)
            rootLayout.addView(scrollView)
        }

        val divider = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1
            ).also {
                it.topMargin    = (12 * density).toInt()
                it.bottomMargin = (12 * density).toInt()
            }
            setBackgroundColor(Color.parseColor("#E0E0E0"))
        }
        rootLayout.addView(divider)

        // New comment label
        val newCommentLabel = TextView(this).apply {
            text = "Tulis komentar"
            textSize = 13f
            setTextColor(Color.parseColor("#9E9E9E"))
            setPadding(0, 0, 0, (8 * density).toInt())
        }
        rootLayout.addView(newCommentLabel)

        // Input field for new comment
        val inputField = EditText(this).apply {
            hint = "Tulis komentarmu di sini..."
            setHintTextColor(Color.parseColor("#BDBDBD"))
            textSize = 14f
            setTextColor(Color.parseColor("#1A1A1A"))
            inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
            maxLines = 3
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.parseColor("#FFF5F5"))
                cornerRadius = 8f * density
                setStroke((1f * density).toInt(), Color.parseColor("#F5C6C6"))
            }
            setPadding(
                (12 * density).toInt(), (10 * density).toInt(),
                (12 * density).toInt(), (10 * density).toInt()
            )
        }
        rootLayout.addView(inputField)

        // Build and show dialog
        AlertDialog.Builder(this)
            .setTitle("Komentar")
            .setView(rootLayout)
            .setPositiveButton("Kirim") { _, _ ->
                val commentText = inputField.text.toString().trim()
                if (commentText.isNotEmpty()) {
                    post.commentList.add(Comment("Kamu", commentText))
                    adapter.notifyItemChanged(postPosition)
                    Toast.makeText(this, "Komentar terkirim!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Tutup", null)
            .show()
    }

    private fun submitPost() {
        val text = inputPost.text.toString().trim()
        if (text.isEmpty()) {
            Toast.makeText(this, "Tuliskan sesuatu dulu ya!", Toast.LENGTH_SHORT).show()
            return
        }

        posts.add(0, ForumPost(
            authorName  = "Kamu",
            date        = "Baru saja",
            content     = text,
            commentList = mutableListOf()
        ))
        adapter.notifyItemInserted(0)
        recyclerView.scrollToPosition(0)
        inputPost.setText("")
        Toast.makeText(this, "Postingan berhasil dibagikan!", Toast.LENGTH_SHORT).show()
    }

    private fun setupNavigation() {
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        findViewById<LinearLayout>(R.id.navChat).setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
            finish()
        }
        findViewById<LinearLayout>(R.id.navScan).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        findViewById<LinearLayout>(R.id.navUser).setOnClickListener {
            Toast.makeText(this, "Profile coming soon!", Toast.LENGTH_SHORT).show()
        }
    }
}