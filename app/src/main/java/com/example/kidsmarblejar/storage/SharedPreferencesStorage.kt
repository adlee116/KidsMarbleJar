package com.example.templatewithkoininjection.storage

class SharedPreferencesStorage(private val preferences: Preferences): LocalStorageInterface {

    override fun getCurrentPosition(): Int {
        return preferences.getInt(PAGE_NUMBER, 1)
    }

    override fun storeCurrentPosition(currentPosition: Int) {
        preferences.insert(PAGE_NUMBER, currentPosition)
    }


    companion object {
        const val PAGE_NUMBER = "pageNumber"
    }

}