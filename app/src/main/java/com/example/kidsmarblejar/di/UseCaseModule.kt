package com.example.kidsmarblejar.di

import com.example.kidsmarblejar.domain.*
import org.koin.dsl.module

val useCaseModule = module {

    single { GetAllUsersUseCase(get()) }
    single { GetUserUseCase(get()) }
    single { AddUserUseCase(get()) }
    single { SaveUserUseCase(get()) }
    single { UpdateUserUseCase(get()) }
}