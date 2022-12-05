package com.example.templatewithkoininjection.storage

import android.content.SharedPreferences
import com.google.gson.Gson

class Preferences(
    preferences: SharedPreferences,
    gson: Gson
): BasePreferences(preferences, gson) {
    companion object {
        const val FILE_NAME = "StorageDataRepository"
    }
}