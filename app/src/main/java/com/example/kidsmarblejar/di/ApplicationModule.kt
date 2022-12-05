package com.example.kidsmarblejar.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kidsmarblejar.storage.MarbleAppDatabase
import com.example.templatewithkoininjection.storage.LocalStorageInterface
import com.example.templatewithkoininjection.storage.Preferences
import com.example.templatewithkoininjection.storage.SharedPreferencesStorage
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val applicationModule = module {

    single<SharedPreferences>(named(Preferences.FILE_NAME)) {
        androidContext().getSharedPreferences(Preferences.FILE_NAME, Context.MODE_PRIVATE)
    }

    single<LocalStorageInterface>{SharedPreferencesStorage(get())}
    single {Preferences(get(named(Preferences.FILE_NAME)), get())}
    single { GsonBuilder().addSerializationExclusionStrategy(get())}
    single<Gson> { get<GsonBuilder>().create()}

    single {
        Room.databaseBuilder(
            androidContext(),
            MarbleAppDatabase::class.java,
            MarbleAppDatabase.NAME
        ).fallbackToDestructiveMigration()
            .build()
    }

}