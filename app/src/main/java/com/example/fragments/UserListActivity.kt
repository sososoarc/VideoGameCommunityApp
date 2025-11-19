package com.example.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fragments.databinding.ActivityChatlistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatlistBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val users = mutableListOf<User>()
    private val filteredUsers = mutableListOf<User>()

    private lateinit var searchAdapter: UserAdapter
    private lateinit var chatAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recycler de búsquedas
        searchAdapter = UserAdapter(filteredUsers) {
            openChat(it.uid)
        }
        binding.recyclerSearch.layoutManager = LinearLayoutManager(this)
        binding.recyclerSearch.adapter = searchAdapter

        // Recycler de "chats recientes" (por ahora mismos usuarios)
        chatAdapter = UserAdapter(users) {
            openChat(it.uid)
        }
        binding.recyclerChats.layoutManager = LinearLayoutManager(this)
        binding.recyclerChats.adapter = chatAdapter

        setupSearch()
        loadUsers()
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()

                if (query.isEmpty()) {
                    binding.recyclerSearch.visibility = View.GONE
                    filteredUsers.clear()
                    searchAdapter.notifyDataSetChanged()
                } else {
                    binding.recyclerSearch.visibility = View.VISIBLE
                    filteredUsers.clear()
                    filteredUsers.addAll(
                        users.filter {
                            it.username.lowercase().contains(query)
                        }
                    )
                    searchAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun loadUsers() {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                users.clear()

                for (doc in result) {
                    val user = doc.toObject(User::class.java)

                    if (user.uid != auth.currentUser!!.uid) {
                        users.add(user)
                    }
                }

                chatAdapter.notifyDataSetChanged()
            }
    }

    private fun openChat(otherUid: String) {
        val myUid = auth.currentUser!!.uid
        val chatId = listOf(myUid, otherUid).sorted().joinToString("_")

        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("CHAT_ID", chatId)
        intent.putExtra("OTHER_UID", otherUid)
        startActivity(intent)
    }
}
