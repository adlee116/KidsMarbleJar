package com.example.kidsmarblejar

import android.app.Application
import com.example.kidsmarblejar.di.applicationModule
import com.example.kidsmarblejar.di.repositoryModule
import com.example.kidsmarblejar.di.useCaseModule
import com.example.kidsmarblejar.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KidsMarbleJar: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@KidsMarbleJar)
            modules(
                listOf(
                    viewModelModule,
                    applicationModule,
                    repositoryModule,
                    useCaseModule
                )
            )
        }
    }
}