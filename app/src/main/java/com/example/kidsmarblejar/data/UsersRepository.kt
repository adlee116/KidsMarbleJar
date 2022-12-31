package com.example.kidsmarblejar.data

import com.example.kidsmarblejar.presentation.home.UserEntity
import com.example.kidsmarblejar.storage.MarbleAppDatabase

class UsersRepository(private val db: MarbleAppDatabase) {

    suspend fun getAllUsers(): List<UserEntity> {
        return db.userDao().getAllUsers()
    }

    suspend fun getUser(id: Int): UserEntity {
        return db.userDao().getUser(id)
    }

    suspend fun addUser(user: UserEntity): Int {
        val userId = db.userDao().insertUser(user)
        return userId.toInt()
    }

    suspend fun updateUser(user: UserEntity) {
        db.userDao().updateUser(user)
    }
}