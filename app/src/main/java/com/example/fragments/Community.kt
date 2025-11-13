package com.example.fragments

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fragments.AppDatabase
import com.example.fragments.DatabaseProvider
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class Community : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: PostAdapter
    private lateinit var db: AppDatabase
    private lateinit var postDao: PostDao
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var bottomNavigation: BottomNavigationView

    private val items: MutableList<Post> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)

        initUi()
        initDatabase()
        observePosts()
        setupFab()
        setupBottomNav()
    }

    private fun initUi() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        recycler = findViewById(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.setHasFixedSize(true)
        recycler.itemAnimator = DefaultItemAnimator()

        adapter = PostAdapter(
            items = items,
            onLike = { post ->
                Toast.makeText(this, "Like a ${post.name}", Toast.LENGTH_SHORT).show()
            },
            onReply = { post ->
                Toast.makeText(this, "Comentar a ${post.name}", Toast.LENGTH_SHORT).show()
            },
            onLongPress = { post ->
                confirmDelete(post)
            }
        )
        recycler.adapter = adapter
    }

    private fun setupFab() {
        fabAdd = findViewById(R.id.fabAdd)
        fabAdd.setOnClickListener {
            startActivity(Intent(this, CommunityPost::class.java))
        }
    }

    private fun setupBottomNav() {
        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_community

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_community -> true
                R.id.nav_achievements -> {
                    startActivity(Intent(this, AchievementsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_chats -> {
                    startActivity(Intent(this, ChatListActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }

    private fun initDatabase() {
        db = DatabaseProvider.getDatabase(this)
        postDao = db.postDao()
    }

    private fun observePosts() {
        lifecycleScope.launch {
            postDao.getAllFlow().collectLatest { posts ->
                items.clear()
                items.addAll(posts)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun confirmDelete(post: Post) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Eliminar publicación")
            .setMessage("¿Quieres eliminar esta publicación?")
            .setPositiveButton("Eliminar") { d, _ ->
                deletePost(post)
                d.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deletePost(post: Post) {
        lifecycleScope.launch(Dispatchers.IO) {
            postDao.deleteById(post.id)
        }
    }
}
