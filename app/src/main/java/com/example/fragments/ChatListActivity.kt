package com.example.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fragments.databinding.ActivityChatlistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatlistBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val allUsers = ArrayList<User>()
    private val chatUsers = ArrayList<User>()

    private lateinit var userAdapter: SearchUserAdapter
    private lateinit var chatAdapter: SearchUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAdapters()
        setupSearchBar()
        loadAllUsers()
        loadMyChats()
    }

    private fun setupAdapters() {

        // Lista de chats / conversaciones
        chatAdapter = SearchUserAdapter(chatUsers) { user ->
            openChat(user.uid)
        }
        binding.recyclerChats.layoutManager = LinearLayoutManager(this)
        binding.recyclerChats.adapter = chatAdapter

        // Lista de búsqueda
        userAdapter = SearchUserAdapter(allUsers) { user ->
            openChat(user.uid)
        }
        binding.recyclerSearch.layoutManager = LinearLayoutManager(this)
        binding.recyclerSearch.adapter = userAdapter
    }

    private fun setupSearchBar() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                val query = text.toString().trim().lowercase()

                if (query.isEmpty()) {
                    binding.recyclerSearch.adapter = null
                    binding.recyclerSearch.visibility = android.view.View.GONE
                } else {
                    val filtered = allUsers.filter {
                        it.username.lowercase().contains(query)
                    }

                    binding.recyclerSearch.visibility = android.view.View.VISIBLE
                    userAdapter = SearchUserAdapter(filtered) { user ->
                        openChat(user.uid)
                    }
                    binding.recyclerSearch.adapter = userAdapter
                }
            }
        })
    }

    private fun loadAllUsers() {
        db.collection("users").get().addOnSuccessListener { docs ->
            allUsers.clear()
            for (doc in docs) {
                val user = doc.toObject(User::class.java)
                if (user.uid != auth.currentUser?.uid) {
                    allUsers.add(user)
                }
            }
        }
    }

    private fun loadMyChats() {
        val myId = auth.currentUser!!.uid

        db.collection("chats")
            .document(myId)
            .collection("conversations")
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    chatUsers.clear()

                    val ids = value.documents.map { it.id }

                    if (ids.isEmpty()) {
                        chatAdapter.notifyDataSetChanged()
                        return@addSnapshotListener
                    }

                    db.collection("users")
                        .whereIn("uid", ids)
                        .get()
                        .addOnSuccessListener { userDocs ->
                            for (doc in userDocs) {
                                chatUsers.add(doc.toObject(User::class.java))
                            }
                            chatAdapter.notifyDataSetChanged()
                        }
                }
            }
    }

    private fun openChat(userId: String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("chatUserId", userId)
        startActivity(intent)
    }
}
