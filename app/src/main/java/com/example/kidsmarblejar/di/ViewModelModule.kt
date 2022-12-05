package com.example.kidsmarblejar.di

import com.example.kidsmarblejar.presentation.addUser.AddUserViewModel
import com.example.kidsmarblejar.presentation.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { HomeViewModel(get()) }
    viewModel { AddUserViewModel(get()) }

}