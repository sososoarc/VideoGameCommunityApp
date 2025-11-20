package com.example.fragments

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fragments.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Si ya está logueado, pasar directo
        auth.currentUser?.let {
            goToHome()
        }

        // LOGIN
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    goToHome()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }
        }

        // IR A REGISTRO
        binding.btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // RECUPERAR CONTRASEÑA (solo una vez)
        binding.tvForgotPassword.setOnClickListener {
            showRecoverDialog()
        }
    }

    // ---- RECUPERAR CONTRASEÑA ----
    private fun showRecoverDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_recover_password, null)

        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val etEmail = dialogView.findViewById<android.widget.EditText>(R.id.etRecoveryEmail)
        val btnSend = dialogView.findViewById<android.widget.Button>(R.id.btnSendRecovery)

        btnSend.setOnClickListener {
            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Ingresa un correo válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(this, "Correo enviado. Revisa tu bandeja", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        dialog.show()
    }

    // ---- IR A HOME ----
    private fun goToHome() {
        startActivity(Intent(this, Community::class.java))
        finish()
    }
}
