package com.example.templatewithkoininjection.storage

class LocalStorageRepository(private val storageLocation: LocalStorageInterface) {

    fun getCurrentPage(): Int {
        return storageLocation.getCurrentPosition()
    }
}