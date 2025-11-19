package com.example.fragments

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fragments.databinding.ItemUserBinding

class UserAdapter(
    private var userList: List<User>,
    private val onUserClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]

        holder.binding.tvUsername.text = user.username

        Glide.with(holder.itemView.context)
            .load(user.profileImage)
            .circleCrop()
            .placeholder(R.drawable.ic_user_placeholder)
            .into(holder.binding.ivProfile)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("RECIPIENT_ID", user.uid)
            intent.putExtra("RECIPIENT_NAME", user.username)
            intent.putExtra("RECIPIENT_IMG", user.profileImage)

            context.startActivity(intent)
        }
    }

    override fun getItemCount() = userList.size

    fun updateList(newList: List<User>) {
        userList = newList
        notifyDataSetChanged()
    }
}
