package com.example.fragments

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.fragments.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Si no hay usuario, regresar al login
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        loadUserData()

        binding.ivProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // Recargar datos cuando regreses del EditProfile
        if (auth.currentUser != null) loadUserData()
    }

    private fun loadUserData() {
        val uid = auth.currentUser!!.uid

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    Toast.makeText(this, "No se encontró el usuario", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val username = doc.getString("username") ?: "Usuario"
                val profileImage = doc.getString("profileImage") ?: ""

                binding.tvUsername.text = username

                // Manejo seguro de la imagen
                if (profileImage.isEmpty()) {
                    binding.ivProfile.setImageResource(android.R.drawable.sym_def_app_icon)
                } else {
                    Glide.with(this)
                        .load(profileImage)
                        .centerCrop()
                        .placeholder(android.R.drawable.sym_def_app_icon)
                        .into(binding.ivProfile)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error cargando datos", Toast.LENGTH_SHORT).show()
            }
    }
}
