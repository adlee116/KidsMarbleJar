package com.example.kidsmarblejar.di

import com.example.kidsmarblejar.data.UsersRepository
import com.example.kidsmarblejar.storage.UserDao
import com.example.templatewithkoininjection.storage.SharedPreferencesStorage
import org.koin.dsl.module

val repositoryModule = module {

    single { SharedPreferencesStorage(get()) }
    single { UsersRepository(get()) }
}