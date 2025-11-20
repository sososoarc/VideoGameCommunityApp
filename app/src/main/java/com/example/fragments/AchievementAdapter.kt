package com.example.fragments

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AchievementAdapter(
    private val achievements: MutableList<Achievement>,
    private val onToggle: (Achievement) -> Unit
) : RecyclerView.Adapter<AchievementAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.titleAchievement)
        val status = itemView.findViewById<TextView>(R.id.statusAchievement)
        val check = itemView.findViewById<ImageView>(R.id.checkAchievement)

        fun bind(achievement: Achievement) {
            title.text = achievement.title

            if (achievement.completed) {
                status.text = "Completado"
                status.setTextColor(Color.parseColor("#1B5E20"))
                check.visibility = View.VISIBLE
            } else {
                status.text = "Pendiente"
                status.setTextColor(Color.GRAY)
                check.visibility = View.GONE
            }

            itemView.setOnClickListener {
                achievement.completed = !achievement.completed
                notifyItemChanged(adapterPosition)
                onToggle(achievement)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.achievement_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = achievements.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(achievements[position])
    }
}
