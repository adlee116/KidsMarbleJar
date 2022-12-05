package com.example.kidsmarblejar.di

import com.example.kidsmarblejar.domain.AddUserUseCase
import com.example.kidsmarblejar.domain.GetAllUsersUseCase
import com.example.kidsmarblejar.domain.GetUserUseCase
import com.example.kidsmarblejar.domain.SaveUserUseCase
import org.koin.dsl.module

val useCaseModule = module {

    single { GetAllUsersUseCase(get()) }
    single { GetUserUseCase(get()) }
    single { AddUserUseCase(get()) }
    single { SaveUserUseCase(get()) }
}