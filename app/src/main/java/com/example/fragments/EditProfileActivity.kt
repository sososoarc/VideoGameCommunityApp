package com.example.fragments


import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.fragments.AppDatabase
import com.example.fragments.databinding.ActivityEditProfileBinding
import kotlinx.coroutines.launch
import java.io.IOException

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var db: AppDatabase
    private var email: String? = null
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "user_database"
        ).build()

        email = intent.getStringExtra("USER_EMAIL")

        binding.btnChangePhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSaveChanges.setOnClickListener {
            val newName = binding.etNewUsername.text.toString().trim()
            if (newName.isEmpty()) {
                Toast.makeText(this, "Ingresa un nombre válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = email?.let { db.userDao().getUserByEmail(it) }
                if (user != null) {
                    val updatedUser = user.copy(email = newName)
                    db.userDao().insertUser(updatedUser)
                    runOnUiThread {
                        Toast.makeText(this@EditProfileActivity, "Nombre actualizado", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@EditProfileActivity, HomeActivity::class.java))
                        finish()
                    }
                }
            }
        }

        binding.btnLogout.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            try {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                binding.ivPreview.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
