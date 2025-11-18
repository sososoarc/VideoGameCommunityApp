package com.example.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.fragments.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.bumptech.glide.Glide

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private var imageUri: Uri? = null
    private var uid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        uid = intent.getStringExtra("USER_UID") ?: auth.currentUser!!.uid

        loadUserData()

        binding.btnChangePhoto.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnSaveChanges.setOnClickListener {
            saveChanges()
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadUserData() {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val username = doc.getString("username") ?: ""
                val profileImage = doc.getString("profileImage") ?: ""

                binding.tvUsername.text = username
                binding.etNewUsername.setText(username)

                if (profileImage.isNotEmpty()) {
                    Glide.with(this)
                        .load(profileImage)
                        .into(binding.ivPreview)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveChanges() {
        val newUsername = binding.etNewUsername.text.toString().trim()

        if (newUsername.isEmpty()) {
            Toast.makeText(this, "Ingresa un nombre válido", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri != null) {
            val ref = storage.reference.child("profiles/$uid.jpg")

            ref.putFile(imageUri!!)
                .continueWithTask { ref.downloadUrl }
                .addOnSuccessListener { url ->
                    updateUserFirestore(newUsername, url.toString())
                }
        } else {
            updateUserFirestore(newUsername, null)
        }
    }

    private fun updateUserFirestore(username: String, imageUrl: String?) {
        val updates = mutableMapOf<String, Any>("username" to username)

        if (imageUrl != null) updates["profileImage"] = imageUrl

        db.collection("users").document(uid)
            .update(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                imageUri = it
                binding.ivPreview.setImageURI(it)
            }
        }
}
