package com.example.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton

class PostAdapter(
    private val items: List<Post>,
    private val onLike: (Post) -> Unit = {},
    private val onReply: (Post) -> Unit = {},
    private val onLongPress:(Post)-> Unit = {}
) : RecyclerView.Adapter<PostAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgAvatar: ImageView = itemView.findViewById(R.id.imgAvatar)
        private val txtName: TextView = itemView.findViewById(R.id.txtName)
        private val txtMeta: TextView = itemView.findViewById(R.id.txtMeta)
        private val txtContent: TextView = itemView.findViewById(R.id.txtContent)
        private val btnLike: MaterialButton = itemView.findViewById(R.id.btnLike)
        private val btnReply: MaterialButton = itemView.findViewById(R.id.btnReply)

        fun bind(item: Post) {

            Glide.with(itemView.context)
                .load(item.profileImage)
                .placeholder(R.drawable.bg_avatar)
                .circleCrop()
                .into(imgAvatar)

            txtName.text = item.name
            txtMeta.text = "${item.handle} · ${item.time}"
            txtContent.text = item.content

            btnLike.setOnClickListener { onLike(item) }
            btnReply.setOnClickListener { onReply(item) }

            itemView.setOnLongClickListener {
                onLongPress(item)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
