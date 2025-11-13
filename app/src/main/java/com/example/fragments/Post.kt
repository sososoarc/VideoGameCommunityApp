package com.example.fragments

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val handle: String,
    val time: String,
    val content: String,
    val liked: Boolean = false,
    val avatarRes: Int = 0 // Valor por defecto para evitar conflictos
)
