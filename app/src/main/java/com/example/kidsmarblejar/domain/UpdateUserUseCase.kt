package com.example.kidsmarblejar.domain

import com.example.kidsmarblejar.core.BaseUseCase
import com.example.kidsmarblejar.core.Result
import com.example.kidsmarblejar.data.UsersRepository
import com.example.kidsmarblejar.presentation.home.UserEntity

class UpdateUserUseCase(private val usersRepository: UsersRepository): BaseUseCase<UserEntity, UpdateUserUseCase.Params>() {

    override suspend fun run(params: Params): Result<UserEntity, Exception> {
        return try {
            val updatedUser = updateUser(params)
            usersRepository.updateUser(updatedUser)
            Result.Success(updatedUser)
        } catch (ex: Exception) {
            Result.Failure(ex)
        }
    }

    data class Params(
        val userEntity: UserEntity,
        val name: String = userEntity.name,
        val image: String = userEntity.image,
        val marbles: Int = userEntity.marbles,
        val conversionType: Int = userEntity.conversionType,
        val conversionValue: Double = userEntity.conversionValue,
        val requiresPassword: Boolean = userEntity.requiresPassword,
        val goal: Int = userEntity.goal,
        val goalName: String = userEntity.goalName,
        val isAdult: Boolean = userEntity.isAdult
    )

}

fun updateUser(params: UpdateUserUseCase.Params): UserEntity {
    return UserEntity(
        params.userEntity.id,
        params.name,
        params.image,
        params.marbles,
        params.conversionType,
        params.conversionValue,
        params.requiresPassword,
        params.goal,
        params.goalName,
        params.isAdult
    )
}