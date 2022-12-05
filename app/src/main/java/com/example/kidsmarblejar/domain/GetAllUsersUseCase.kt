package com.example.kidsmarblejar.domain

import com.example.kidsmarblejar.core.EmptyParamsUseCase
import com.example.kidsmarblejar.data.UsersRepository
import com.example.kidsmarblejar.presentation.home.UserEntity

class GetAllUsersUseCase(private val usersRepository: UsersRepository): EmptyParamsUseCase<List<UserEntity>>() {
    override suspend fun run() = usersRepository.getAllUsers()
}