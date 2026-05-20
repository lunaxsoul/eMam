package com.example.emam

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

data class ForumPost(
    val authorName: String,
    val date: String,
    val content: String,
    var likes: Int = 0,
    var comments: Int = 0,
    var bookmarks: Int = 0,
    var isLiked: Boolean = false,
    var isBookmarked: Boolean = false
)

class ForumAdapter(private val posts: MutableList<ForumPost>) :
    RecyclerView.Adapter<ForumAdapter.PostViewHolder>() {

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val authorName: TextView    = view.findViewById(R.id.postAuthorName)
        val postDate: TextView      = view.findViewById(R.id.postDate)
        val postContent: TextView   = view.findViewById(R.id.postContent)
        val btnLike: LinearLayout   = view.findViewById(R.id.btnLike)
        val btnComment: LinearLayout = view.findViewById(R.id.btnComment)
        val btnBookmark: LinearLayout = view.findViewById(R.id.btnBookmark)
        val likeCount: TextView     = view.findViewById(R.id.likeCount)
        val commentCount: TextView  = view.findViewById(R.id.commentCount)
        val bookmarkCount: TextView = view.findViewById(R.id.bookmarkCount)
        val likeIcon: TextView      = view.findViewById(R.id.likeIcon)
        val bookmarkIcon: TextView  = view.findViewById(R.id.bookmarkIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forum_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        holder.authorName.text  = post.authorName
        holder.postDate.text    = post.date
        holder.postContent.text = post.content
        holder.likeCount.text   = post.likes.toString()
        holder.commentCount.text = post.comments.toString()
        holder.bookmarkCount.text = post.bookmarks.toString()

        // Like icon state
        holder.likeIcon.text = if (post.isLiked) "♥" else "♡"
        holder.likeIcon.setTextColor(
            if (post.isLiked) Color.parseColor("#C62828") else Color.parseColor("#9E9E9E")
        )

        // Bookmark icon state
        holder.bookmarkIcon.text = if (post.isBookmarked) "🔖" else "🏷"

        // Like button
        holder.btnLike.setOnClickListener {
            val p = posts[holder.adapterPosition]
            p.isLiked = !p.isLiked
            p.likes   = if (p.isLiked) p.likes + 1 else p.likes - 1
            notifyItemChanged(holder.adapterPosition)
        }

        // Bookmark button
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

        // Input bar background
        postInputBar.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.WHITE)
            cornerRadius = 12f * density
            setStroke((1f * density).toInt(), Color.parseColor("#E0E0E0"))
        }

        // Post button
        btnPost.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.parseColor("#C62828"))
            cornerRadius = 8f * density
        }
    }

    private fun loadSamplePosts() {
        posts.add(ForumPost(
            authorName = "Misca Alexandra",
            date       = "20 September 2025",
            content    = "Halo bunda-bunda!\n\nAku mau sharing sedikit menu yang sering aku makan supaya kebutuhan nutrisi tetap terpenuhi selama hamil.\n\nBeberapa makanan yang rutin aku konsumsi:\n🥚 Telur rebus – sumber protein dan vitamin D\n🥦 Bayam dan brokoli – kaya zat besi dan folat\n🐟 Ikan kembung atau ikan salmon – tinggi omega-3 untuk perkembangan otak janin\n🥛 Susu ibu hamil – membantu memenuhi kebutuhan kalsium\n\nBiasanya aku juga tambah tempe atau tahu sebagai sumber protein nabati.\nSemoga membantu ya bunda 💛",
            likes      = 14,
            comments   = 8,
            bookmarks  = 90
        ))
        posts.add(ForumPost(
            authorName = "Sari Dewi",
            date       = "18 September 2025",
            content    = "Bunda-bunda ada yang punya tips buat atasi mual di trimester pertama?\n\nAku udah coba jahe hangat tapi masih sering mual pas pagi hari 😢\n\nMohon sharingnya ya!",
            likes      = 23,
            comments   = 15,
            bookmarks  = 45
        ))
        posts.add(ForumPost(
            authorName = "Rina Pratiwi",
            date       = "15 September 2025",
            content    = "Alhamdulillah minggu ini hasil USG bagus! Berat badan bayi sesuai dengan usia kehamilan 28 minggu 🎉\n\nDokter menyarankan untuk tetap jaga asupan zat besi karena kadar HB masih sedikit rendah. Ada yang punya rekomendasi makanan tinggi zat besi yang enak?",
            likes      = 31,
            comments   = 22,
            bookmarks  = 67
        ))
    }

    private fun setupRecyclerView() {
        adapter = ForumAdapter(posts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun submitPost() {
        val text = inputPost.text.toString().trim()
        if (text.isEmpty()) {
            Toast.makeText(this, "Tuliskan sesuatu dulu ya!", Toast.LENGTH_SHORT).show()
            return
        }

        val newPost = ForumPost(
            authorName = "Kamu",
            date       = "Baru saja",
            content    = text,
            likes      = 0,
            comments   = 0,
            bookmarks  = 0
        )

        posts.add(0, newPost)
        adapter.notifyItemInserted(0)
        recyclerView.scrollToPosition(0)
        inputPost.setText("")

        Toast.makeText(this, "Postingan berhasil dibagikan!", Toast.LENGTH_SHORT).show()
    }

    private fun setupNavigation() {
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
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