package com.example.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fragments.databinding.ItemUserSearchBinding
import com.squareup.picasso.Picasso

class SearchUserAdapter(
    private val users: List<User>,
    private val onUserClick: (User) -> Unit
) : RecyclerView.Adapter<SearchUserAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: ItemUserSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.tvName.text = user.username

            if (user.profileImage.isNotEmpty()) {
                Picasso.get().load(user.profileImage).into(binding.ivProfile)
            }

            binding.root.setOnClickListener {
                onUserClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size
}
