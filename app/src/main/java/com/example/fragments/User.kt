package com.example.fragments

data class   User(
    val uid: String = "",
    val email: String = "",
    val username: String = "",
    val profileImage: String = "",   // URL de foto
    val lastMessage: String = "",    // útil para lista de chats
    val timestamp: Long = 0L         // para ordenar chats
)
