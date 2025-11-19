package com.example.fragments

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fragments.databinding.ActivitySearchUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SearchUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchUserBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userList = ArrayList<User>()
    private lateinit var adapter: SearchUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = SearchUserAdapter(userList) { selectedUser ->
            // Abrir chat con este usuario
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("uid", selectedUser.uid)
            intent.putExtra("name", selectedUser.username)
            startActivity(intent)
        }

        binding.recyclerUsers.layoutManager = LinearLayoutManager(this)
        binding.recyclerUsers.adapter = adapter

        loadUsers()
    }

    private fun loadUsers() {
        db.collection("users").get()
            .addOnSuccessListener { documents ->
                userList.clear()
                for (doc in documents) {
                    val user = doc.toObject(User::class.java)

                    // No mostrarte a ti misma
                    if (user.uid != auth.currentUser!!.uid) {
                        userList.add(user)
                    }
                }
                adapter.notifyDataSetChanged()
            }
    }
}
