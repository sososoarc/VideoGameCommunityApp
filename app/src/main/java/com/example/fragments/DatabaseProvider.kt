package com.example.fragments

import android.content.Context
import androidx.room.Room


object DatabaseProvider {
    @Volatile private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase =
        INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "posts.db"
            ).fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
        }
}
