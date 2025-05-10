package edu.ifsp.com.br.redesocialrc

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import edu.ifsp.com.br.redesocialrc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.currentUser != null){
            startActivity(Intent(this, FeedActivity::class.java))
            finish()
        }

        binding.buttonLogin.setOnClickListener {
            val email = binding.textEmail.text.toString()
            val senha = binding.textPassword.text.toString()
            firebaseAuth
                .signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, FeedActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
        }

        binding.buttonCreate.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }
}