package com.example.fragments


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.fragments.AppDatabase
import com.example.fragments.User
import com.example.fragments.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var db: AppDatabase
    private var currentUser: User? = null
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "user_database"
        ).build()

        val email = intent.getStringExtra("USER_EMAIL")

        // Cargar usuario local o de Google
        GlobalScope.launch {
            currentUser = email?.let { db.userDao().getUserByEmail(it) }
            runOnUiThread {
                binding.tvUsername.text = currentUser?.email ?: firebaseAuth.currentUser?.displayName ?: "Usuario"
                binding.ivProfile.setImageResource(android.R.drawable.sym_def_app_icon)
            }
        }

        // Navegaciones (las pantallas se harán después)
        binding.btnComunidad.setOnClickListener {
            Toast.makeText(this, "Comunidad próximamente 💬", Toast.LENGTH_SHORT).show()
        }
        binding.btnChat.setOnClickListener {
            Toast.makeText(this, "Chat próximamente 💭", Toast.LENGTH_SHORT).show()
        }
        binding.btnLogros.setOnClickListener {
            Toast.makeText(this, "Logros próximamente 🏅", Toast.LENGTH_SHORT).show()
        }

        // Click en el perfil
        binding.ivProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("USER_EMAIL", email)
            startActivity(intent)
        }
    }
}
