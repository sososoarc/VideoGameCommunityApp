package com.example.fragments

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fragments.databinding.ActivityCommunityPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class CommunityPost : AppCompatActivity() {

    private lateinit var binding: ActivityCommunityPostBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var name = ""
    private var handle = ""
    private var profileImage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadUserData()

        binding.btnPublicar.setOnClickListener {
            val content = binding.etContent.text.toString().trim()

            if (content.isEmpty()) {
                Toast.makeText(this, "Escribe algo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            publishPost(content)
        }
    }

    private fun loadUserData() {
        val uid = auth.currentUser!!.uid

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                name = doc.getString("username") ?: "Usuario"
                handle = doc.getString("handle") ?: "@usuario"
                profileImage = doc.getString("profileImage") ?: ""
            }
    }

    private fun publishPost(content: String) {
        val postId = UUID.randomUUID().toString()
        val uid = auth.currentUser!!.uid
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        val post = Post(
            id = postId,
            userId = uid,
            name = name,
            handle = handle,
            profileImage = profileImage,
            time = time,
            content = content
        )

        db.collection("posts").document(postId)
            .set(post)
            .addOnSuccessListener {
                Toast.makeText(this, "Publicado", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, Community::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al publicar", Toast.LENGTH_SHORT).show()
            }
    }
}
