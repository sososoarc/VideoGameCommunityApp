package com.example.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fragments.databinding.ItemMessageLeftBinding
import com.example.fragments.databinding.ItemMessageRightBinding
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter(private var messageList: List<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_RIGHT = 1
    private val ITEM_LEFT = 2
    private val currentUid = FirebaseAuth.getInstance().currentUser?.uid

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].senderId == currentUid) ITEM_RIGHT else ITEM_LEFT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_RIGHT) {
            val binding = ItemMessageRightBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            RightViewHolder(binding)
        } else {
            val binding = ItemMessageLeftBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            LeftViewHolder(binding)
        }
    }

    override fun getItemCount(): Int = messageList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messageList[position]
        when (holder) {
            is RightViewHolder -> holder.bind(msg)
            is LeftViewHolder -> holder.bind(msg)
        }
    }

    fun updateMessages(list: List<Message>) {
        messageList = list
        notifyDataSetChanged()
    }

    class RightViewHolder(private val binding: ItemMessageRightBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(msg: Message) {
            binding.tvMessageRight.text = msg.message
        }
    }

    class LeftViewHolder(private val binding: ItemMessageLeftBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(msg: Message) {
            binding.tvMessageLeft.text = msg.message
        }
    }
}
