package com.example.fragments

data class Post(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val handle: String = "",
    val time: String = "",
    val content: String = "",
    val liked: Boolean = false,
    val profileImage: String? = null
)

