package com.example.fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fragments.databinding.ItemReceiveBinding
import com.example.fragments.databinding.ItemSendBinding
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(
    private val context: Context,
    private var messageList: ArrayList<Message>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_SEND = 1
    private val ITEM_RECEIVE = 2

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid

        return if (message.senderId == currentUid) ITEM_SEND else ITEM_RECEIVE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SEND) {
            val binding = ItemSendBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
            SendViewHolder(binding)

        } else {
            val binding = ItemReceiveBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
            ReceiveViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]

        when (holder) {
            is SendViewHolder -> {
                holder.binding.txtSendMessage.text = message.message
            }
            is ReceiveViewHolder -> {
                holder.binding.txtReceiveMessage.text = message.message
            }
        }
    }

    override fun getItemCount() = messageList.size

    fun updateMessages(newList: ArrayList<Message>) {
        messageList = newList
        notifyDataSetChanged()
    }

    inner class SendViewHolder(val binding: ItemSendBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class ReceiveViewHolder(val binding: ItemReceiveBinding) :
        RecyclerView.ViewHolder(binding.root)
}
