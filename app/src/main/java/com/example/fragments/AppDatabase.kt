package com.example.fragments

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fragments.Post
import com.example.fragments.PostDao
import com.example.fragments.User
import com.example.fragments.UserDao

@Database(entities = [User::class, Post::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
}
