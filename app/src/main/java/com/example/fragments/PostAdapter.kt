package com.example.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class PostAdapter(
    private val items: List<Post>,
    private val onLike: (Post) -> Unit = {},
    private val onReply: (Post) -> Unit = {},
    private val onLongPress:(Post)-> Unit ={}
) : RecyclerView.Adapter<PostAdapter.VH>() {

    // ViewHolder: mantiene las referencias a los elementos del layout item_post.xml
    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgAvatar: ImageView = itemView.findViewById(R.id.imgAvatar)
        private val txtName: TextView = itemView.findViewById(R.id.txtName)
        private val txtMeta: TextView = itemView.findViewById(R.id.txtMeta)
        private val txtContent: TextView = itemView.findViewById(R.id.txtContent)
        private val btnLike: MaterialButton = itemView.findViewById(R.id.btnLike)
        private val btnReply: MaterialButton = itemView.findViewById(R.id.btnReply)


        fun bind(item: Post) {
            txtName.text = item.name
            txtMeta.text = "${item.handle} · ${item.time}"
            txtContent.text = item.content

            // Mostrar avatar (el Post debe tener avatarRes: Int con drawable)
            imgAvatar.setImageResource(item.avatarRes)

            // Click listeners
            btnLike.setOnClickListener { onLike(item) }
            btnReply.setOnClickListener { onReply(item) }

            itemView.setOnClickListener{
                onLongPress(item)
                true
            }

        }

    }



    // Inflar item_post.xml para cada ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return VH(view)
    }

    // Asignar datos a cada ViewHolder
    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    // Cantidad de items en la lista
    override fun getItemCount(): Int = items.size
}
