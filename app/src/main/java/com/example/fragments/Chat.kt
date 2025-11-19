package com.example.fragments

data class Chat(
    val chatId: String = "",
    val users: List<String> = listOf(),
    val lastMessage: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
