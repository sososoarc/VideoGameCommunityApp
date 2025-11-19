package com.example.fragments

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fragments.databinding.ActivityCommunityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class Community : AppCompatActivity() {

    private lateinit var binding: ActivityCommunityBinding
    private lateinit var adapter: PostAdapter
    private val items = mutableListOf<Post>()

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecycler()
        setupBottomNav()
        setupFab()
        setupProfileButton()
        listenForPosts()   // ← AQUÍ cargamos los posts en tiempo real
    }


    private fun setupRecycler() {
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.setHasFixedSize(true)
        binding.recycler.itemAnimator = DefaultItemAnimator()

        adapter = PostAdapter(
            items,
            onLike = { post ->
                Toast.makeText(this, "Like ♥ a ${post.name}", Toast.LENGTH_SHORT).show()
            },
            onReply = { post ->
                Toast.makeText(this, "Responder a ${post.name}", Toast.LENGTH_SHORT).show()
            },
            onLongPress = { post ->
                confirmDelete(post)
            }
        )

        binding.recycler.adapter = adapter
    }


    private fun listenForPosts() {
        db.collection("posts")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener

                items.clear()
                for (doc in value!!) {
                    val post = doc.toObject(Post::class.java)
                    items.add(post)
                }
                adapter.notifyDataSetChanged()
            }
    }


    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, CommunityPost::class.java))
        }
    }


    private fun setupProfileButton() {
        binding.profile.setOnClickListener {
            val i = Intent(this, EditProfileActivity::class.java)
            startActivity(i)
        }
    }


    private fun setupBottomNav() {
        binding.bottomNavigation.selectedItemId = R.id.nav_community

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_community -> true
                R.id.nav_achievements -> {
                    startActivity(Intent(this, AchievementsActivity::class.java))
                    true
                }
                R.id.nav_chats -> {
                    startActivity(Intent(this, UserListActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }


    private fun confirmDelete(post: Post) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Eliminar publicación")
            .setMessage("¿Quieres eliminar esta publicación?")
            .setPositiveButton("Eliminar") { dialog, _ ->
                deletePost(post)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deletePost(post: Post) {
        db.collection("posts").document(post.id).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
            }
    }
}
