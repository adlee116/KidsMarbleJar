package com.example.kidsmarblejar.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.kidsmarblejar.presentation.home.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM UserEntity WHERE id LIKE :userId LIMIT 1")
    suspend fun getUser(userId: Int): UserEntity

    @Query("SELECT * FROM UserEntity")
    suspend fun getAllUsers(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

}