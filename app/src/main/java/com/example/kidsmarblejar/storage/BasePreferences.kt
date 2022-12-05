package com.example.templatewithkoininjection.storage

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson

open class BasePreferences(
    private val prefs: SharedPreferences,
    private val gson: Gson
) {

    fun getInt(key: String, defValue: Int = 0) = prefs.getInt(key, defValue)
    fun insert(key: String, value: Any?) {
        prefs.edit(commit = true) {
            when (value) {
                is String -> putString(key, value)
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                else -> putString(key, gson.toJson(value))
            }
        }
    }

}