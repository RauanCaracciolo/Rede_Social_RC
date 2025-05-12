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
import edu.ifsp.com.br.redesocialrc.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val galeria = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri
                binding.profileImage.setImageURI(uri)
            } else {
                Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_LONG).show()
            }
        }


        binding.buttonChangePicture.setOnClickListener {
            galeria.launch("image/*")
        }

        binding.save.setOnClickListener {
            saveProfileData()
        }
        binding.buttonSignOut.setOnClickListener{
            signOut()
        }
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
                    val intent = Intent(this, FeedActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)

                    true
                }
                R.id.profileFragment -> {
                    true
                }
                else -> false
            }
        }

        bottomNavigationView.selectedItemId = R.id.profileFragment
    }
    private fun saveProfileData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val email = currentUser.email.toString()
            val nomeCompleto = binding.textNameComplete.text.toString()
            val novaSenha = binding.editNewPassword.text.toString()

            val profileImageDrawable: Drawable? = if (selectedImageUri != null) {
                binding.profileImage.drawable
            } else {
                null
            }

            val fotoPerfilString: String? = if (profileImageDrawable != null) {
                try {
                    Base64Converter.drawableToString(profileImageDrawable)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Erro ao converter a foto", Toast.LENGTH_LONG).show()
                    null
                }
            } else {
                null
            }

            if (fotoPerfilString == null) {
                Toast.makeText(this, "Nenhuma foto de perfil selecionada", Toast.LENGTH_LONG).show()
                return
            }

            val dados = hashMapOf(
                "nomeCompleto" to nomeCompleto,
                "email" to email,
                "fotoPerfil" to fotoPerfilString,
                "senha" to novaSenha // adicionando senha nos dados
            )

            if (novaSenha.isNotEmpty()) {
                currentUser.updatePassword(novaSenha)
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                        Toast.makeText(this, "Erro ao atualizar senha: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }

            db.collection("usuarios").document(userId)
                .set(dados)
                .addOnSuccessListener {
                    startActivity(Intent(this, FeedActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    Toast.makeText(this, "Erro ao salvar os dados: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
    private fun signOut() {
        auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}