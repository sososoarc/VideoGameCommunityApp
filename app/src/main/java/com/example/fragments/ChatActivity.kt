package com.example.fragments

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fragments.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private lateinit var adapter: MessageAdapter
    private val messageList = ArrayList<Message>()

    private lateinit var senderRoom: String
    private lateinit var receiverRoom: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔒 SAFE EXTRAS
        val receiverUid = intent.getStringExtra("RECIPIENT_ID")
        val receiverName = intent.getStringExtra("RECIPIENT_NAME")
        val receiverImage = intent.getStringExtra("RECIPIENT_IMG")

        if (receiverUid == null || receiverName == null) {
            Toast.makeText(this, "Error al abrir el chat", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.tvReceiverName.text = receiverName

        val senderUid = auth.currentUser!!.uid
        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid

        setupRecycler()
        listenMessages()

        binding.btnSend.setOnClickListener {
            sendMessage(senderUid, receiverUid)
        }
    }

    private fun setupRecycler() {
        adapter = MessageAdapter(this, messageList)
        binding.rvMessages.layoutManager = LinearLayoutManager(this)
        binding.rvMessages.adapter = adapter
    }

    private fun listenMessages() {
        db.collection("chats")
            .document(senderRoom)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->

                if (error != null || snapshot == null) return@addSnapshotListener

                messageList.clear()
                for (doc in snapshot.documents) {
                    val msg = doc.toObject(Message::class.java)
                    msg?.let { messageList.add(it) }
                }

                adapter.notifyDataSetChanged()
                binding.rvMessages.scrollToPosition(messageList.size - 1)
            }
    }

    private fun sendMessage(senderUid: String, receiverUid: String) {
        val text = binding.etMessage.text.toString().trim()

        if (text.isEmpty()) {
            Toast.makeText(this, "Escribe algo…", Toast.LENGTH_SHORT).show()
            return
        }

        val message = Message(
            message = text,
            senderId = senderUid,
            timestamp = System.currentTimeMillis()
        )

        // Guardar mensaje en mi chat
        db.collection("chats")
            .document(senderRoom)
            .collection("messages")
            .add(message)
            .addOnSuccessListener {

                // Guardar mensaje en el chat del otro usuario
                db.collection("chats")
                    .document(receiverRoom)
                    .collection("messages")
                    .add(message)
            }

        // Actualizar "recent" del emisor
        db.collection("recent")
            .document(senderUid)
            .collection("list")
            .document(receiverUid)
            .set(
                mapOf(
                    "uid" to receiverUid,
                    "lastMessage" to text,
                    "timestamp" to System.currentTimeMillis()
                )
            )

        // Actualizar "recent" del receptor
        db.collection("recent")
            .document(receiverUid)
            .collection("list")
            .document(senderUid)
            .set(
                mapOf(
                    "uid" to senderUid,
                    "lastMessage" to text,
                    "timestamp" to System.currentTimeMillis()
                )
            )

        binding.etMessage.setText("")
    }
}
