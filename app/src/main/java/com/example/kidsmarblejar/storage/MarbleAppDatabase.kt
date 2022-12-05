package com.example.kidsmarblejar.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.kidsmarblejar.presentation.home.UserEntity
import com.example.kidsmarblejar.storage.MarbleAppDatabase.Companion.VERSION

@Database(
    entities = [UserEntity::class],
    version = VERSION,
    exportSchema = false
)

abstract class MarbleAppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        const val VERSION = 1
        const val NAME = "MARBLE_APP_DATABASE"
    }
}