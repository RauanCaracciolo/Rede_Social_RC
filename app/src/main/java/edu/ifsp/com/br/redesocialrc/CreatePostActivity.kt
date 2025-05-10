package edu.ifsp.com.br.redesocialrc

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import edu.ifsp.com.br.redesocialrc.databinding.ActivityCreatePostBinding

class CreatePostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreatePostBinding
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val galeria = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri
                binding.postImageView.setImageURI(uri)
            } else {
                Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_LONG).show()
            }
        }

        binding.selectImageButton.setOnClickListener {
            galeria.launch("image/*")
        }

        binding.createPostButton.setOnClickListener {
            createPost()
        }

        val bottomNavigationView: BottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.createPostFragment -> {
                    true
                }
                R.id.feedFragment -> {
                    val intent = Intent(this, FeedActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)

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

        bottomNavigationView.selectedItemId = R.id.createPostFragment

    }

    private fun createPost() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val title = binding.postTitleEditText.text.toString()

            val postImageDrawable: Drawable? = if (selectedImageUri != null) {
                binding.postImageView.drawable
            } else {
                null
            }

            val postImageString: String? = if (postImageDrawable != null) {
                try {
                    Base64Converter.drawableToString(postImageDrawable)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Erro ao converter a foto do post", Toast.LENGTH_LONG).show()
                    null
                }
            } else {
                null
            }

            if (postImageString == null) {
                Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_LONG).show()
                return
            }

            db.collection("usuarios").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nomeCompleto = document.getString("nomeCompleto")
                        val userProfilePhotoString = document.getString("fotoPerfil")

                        if (nomeCompleto != null && userProfilePhotoString != null) {
                            val postData = hashMapOf(
                                "title" to title,
                                "postImage" to postImageString,
                                "userName" to nomeCompleto,
                                "userProfilePhoto" to userProfilePhotoString
                            )

                            db.collection("posts").add(postData)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Post criado com sucesso", Toast.LENGTH_LONG).show()
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    e.printStackTrace()
                                    Toast.makeText(this, "Erro ao criar o post: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                        }
                    }
                }.addOnFailureListener { e ->
                    e.printStackTrace()
                    Toast.makeText(this, "Erro ao obter dados do usuário: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}