package com.example.templatewithkoininjection.storage

interface LocalStorageInterface {

    fun getCurrentPosition(): Int

    fun storeCurrentPosition(currentPosition: Int)
}