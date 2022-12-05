package com.example.kidsmarblejar.domain

import com.example.kidsmarblejar.core.BaseUseCase
import com.example.kidsmarblejar.core.Result
import com.example.kidsmarblejar.data.UsersRepository
import com.example.kidsmarblejar.presentation.home.UserEntity

class AddUserUseCase(private val usersRepository: UsersRepository): BaseUseCase<UserEntity, UserEntity>() {
    override suspend fun run(params: UserEntity): Result<UserEntity, Exception> {
        return try {
            val userId = usersRepository.addUser(params)
            Result.Success(usersRepository.getUser(userId))
        } catch (ex: Exception) {
            Result.Failure(ex)
        }
    }
}