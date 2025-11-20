package com.example.fragments

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fragments.databinding.ActivityAchievementsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AchievementsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAchievementsBinding
    private lateinit var adapter: AchievementAdapter
    private val achievements = mutableListOf<Achievement>()

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAchievementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecycler()
        loadAchievementsFromFirebase()
    }

    private fun setupRecycler() {
        adapter = AchievementAdapter(achievements) { achievement ->
            toggleAchievement(achievement)
        }

        binding.recyclerAchievements.layoutManager = LinearLayoutManager(this)
        binding.recyclerAchievements.adapter = adapter
    }

    private fun loadAchievementsFromFirebase() {
        db.collection("users")
            .document(userId)
            .collection("achievements")
            .get()
            .addOnSuccessListener { result ->

                achievements.clear()

                if (result.isEmpty) {
                    // No existen → guardar y cargar por defecto
                    val defaults = defaultAchievements()
                    achievements.addAll(defaults)
                    saveDefaultAchievements(defaults)
                } else {
                    // Ya existen → cargarlos
                    for (doc in result) {
                        val achievement = doc.toObject(Achievement::class.java)
                        achievements.add(achievement)
                    }
                }

                adapter.notifyDataSetChanged()
            }
    }

    private fun defaultAchievements(): List<Achievement> {
        return listOf(
            Achievement(id = "1", title = "Mi primer acertijo resuelto", completed = false),
            Achievement(id = "2", title = "Empecé a jugar el juego", completed = false),
            Achievement(id = "3", title = "Pasé al 2do nivel", completed = false),
            Achievement(id = "4", title = "Terminé el juego", completed = false),
            Achievement(id = "5", title = "Seguí al juego en redes sociales", completed = false),
            Achievement(id = "6", title = "Hice mi primer post en la comunidad", completed = false)
        )
    }

    private fun saveDefaultAchievements(list: List<Achievement>) {
        val ref = db.collection("users")
            .document(userId)
            .collection("achievements")

        list.forEach { ach ->
            ref.document(ach.id).set(ach)
        }
    }

    private fun toggleAchievement(achievement: Achievement) {
        db.collection("users")
            .document(userId)
            .collection("achievements")
            .document(achievement.id)
            .set(achievement)
    }
}
