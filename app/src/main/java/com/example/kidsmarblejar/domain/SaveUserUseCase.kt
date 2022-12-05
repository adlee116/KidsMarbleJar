package com.example.kidsmarblejar.domain

import com.example.kidsmarblejar.core.BaseUseCase
import com.example.kidsmarblejar.core.Result
import com.example.kidsmarblejar.data.UsersRepository
import com.example.kidsmarblejar.presentation.home.UserEntity

class SaveUserUseCase(private val usersRepository: UsersRepository): BaseUseCase<Int, NewUser>() {
    override suspend fun run(params: NewUser): Result<Int, Exception> {
        return try {
            val newUser = createNewUser(params)
            Result.Success(usersRepository.addUser(newUser))
        } catch (ex: Exception) {
            Result.Failure(ex)
        }
    }

    private fun createNewUser(newUser: NewUser): UserEntity {
        return UserEntity(
            name = newUser.name,
            image = newUser.image,
            marbles = newUser.marbles,
            conversionType = newUser.conversionType,
            conversionValue = newUser.conversionValue,
            requiresPassword = newUser.requiresPassword,
            goal = newUser.goal,
            goalName = newUser.goalName,
            isAdult = newUser.isAdult
        )
    }
}

data class NewUser(
    val name: String,
    val image: ByteArray?,
    val marbles: Int,
    val conversionType: Int,
    val conversionValue: Int,
    val requiresPassword: Boolean,
    val goal: Int,
    val goalName: String,
    val isAdult: Boolean
)