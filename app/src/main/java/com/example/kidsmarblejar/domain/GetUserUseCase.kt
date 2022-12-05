package com.example.kidsmarblejar.domain

import com.example.kidsmarblejar.core.BaseUseCase
import com.example.kidsmarblejar.core.Result
import com.example.kidsmarblejar.data.UsersRepository
import com.example.kidsmarblejar.presentation.home.UserEntity

class GetUserUseCase(private val usersRepository: UsersRepository): BaseUseCase<UserEntity, Int>() {
    override suspend fun run(params: Int): Result<UserEntity, Exception> {
        return try {
            Result.Success(usersRepository.getUser(params))
        } catch (ex: Exception) {
            Result.Failure(ex)
        }
    }
}