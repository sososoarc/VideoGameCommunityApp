package com.example.fragments

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class GoogleSignInHelper(private val activity: Activity) {
    private val auth = FirebaseAuth.getInstance()
    private lateinit var client: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("TU_CLIENT_ID_WEB_DE_FIREBASE")
            .requestEmail()
            .build()
        client = GoogleSignIn.getClient(activity, gso)
    }

    fun signIn() {
        val signInIntent = client.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun handleResult(requestCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        Toast.makeText(activity, "Inicio con Google exitoso", Toast.LENGTH_SHORT).show()



                    } else {
                        Toast.makeText(activity, "Error: ${result.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(activity, "Error al iniciar con Google", Toast.LENGTH_SHORT).show()
            }
        }
    }
}