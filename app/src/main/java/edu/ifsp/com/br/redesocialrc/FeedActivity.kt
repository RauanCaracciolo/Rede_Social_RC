package edu.ifsp.com.br.redesocialrc

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import edu.ifsp.com.br.redesocialrc.adapter.PostAdapter
import edu.ifsp.com.br.redesocialrc.databinding.ActivityFeedBinding
import edu.ifsp.com.br.redesocialrc.model.Post

class FeedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedBinding
    private lateinit var adapter: PostAdapter
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        loadPosts()


        val bottomNavigationView: BottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.createPostFragment -> {
                    val intent = Intent(this, CreatePostActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }
                R.id.feedFragment -> {
                    true
                }
                R.id.profileFragment -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)

                    true
                }
                else -> false
            }
        }
        bottomNavigationView.selectedItemId = R.id.feedFragment


    }

    private fun loadPosts() {
        db.collection("posts").get()
            .addOnSuccessListener { result ->
                val posts = ArrayList<Post>()
                for (document in result) {
                    val title = document.getString("title") ?: ""
                    val postImageBase64 = document.getString("postImage") ?: ""
                    val userName = document.getString("userName") ?: ""
                    val userProfilePhotoBase64 = document.getString("userProfilePhoto") ?: ""
                    val postId = document.id

                    val userProfileBitmap = decodeBase64ToBitmap(userProfilePhotoBase64)
                    val postImageBitmap = decodeBase64ToBitmap(postImageBase64)

                    posts.add(Post(postId, userProfileBitmap, userName, title, postImageBitmap))
                }

                adapter = PostAdapter(posts.toTypedArray())
                binding.recycleView.layoutManager = LinearLayoutManager(this)
                binding.recycleView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                Toast.makeText(this, "Error loading posts", Toast.LENGTH_LONG).show()
            }
    }

    private fun decodeBase64ToBitmap(base64String: String): Bitmap? {
        return if (base64String != "") {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } else {
            null
        }
    }
}