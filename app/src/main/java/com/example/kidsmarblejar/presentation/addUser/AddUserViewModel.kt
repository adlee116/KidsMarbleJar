package com.example.kidsmarblejar.presentation.addUser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidsmarblejar.domain.NewUser
import com.example.kidsmarblejar.domain.SaveUserUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AddUserViewModel(
    private val saveUserUseCase: SaveUserUseCase
) : ViewModel() {

    private val _saved = MutableSharedFlow<Unit>()
    val saved = _saved.asSharedFlow()

    private val _saveFailed = MutableSharedFlow<Unit>()
    val saveFailed = _saveFailed.asSharedFlow()

    var savedImage: ByteArray? = null
    var rewardType: Int? = null

    fun process(addUserEvent: AddUserEvent) {
        when (addUserEvent) {
            is AddUserEvent.SaveClicked -> saveUser(addUserEvent)
            AddUserEvent.AddImageClicked -> TODO()
            AddUserEvent.MoneyToggled -> TODO()
            AddUserEvent.ParentToggled -> TODO()
            AddUserEvent.RequiresPasswordToggled -> TODO()
            AddUserEvent.RewardToggled -> TODO()
        }
    }

    private fun saveUser(userEvent: AddUserEvent) {
        val user = convertEventToUserEntity(userEvent)
        saveUserUseCase.invoke(viewModelScope, user) { result ->
            result.result(
                onSuccess = {
                    emitSaved()
                },
                onFailure = {

                }
            )
        }
    }

    private fun convertEventToUserEntity(userEvent: AddUserEvent): NewUser {
        val savedClickedEvent: AddUserEvent.SaveClicked = userEvent as AddUserEvent.SaveClicked
        return NewUser(
            name = savedClickedEvent.name,
            image = savedImage,
            marbles = 0,
            conversionType = savedClickedEvent.conversionType,
            conversionValue = savedClickedEvent.conversionValue,
            requiresPassword = savedClickedEvent.requiresPassword,
            goal = savedClickedEvent.goal,
            goalName = savedClickedEvent.goalName,
            isAdult = savedClickedEvent.isAdult
        )
    }

    private fun emitSaved() {
        viewModelScope.launch {
            _saved.emit(Unit)
        }
    }

    private fun emitFailed() {
        viewModelScope.launch {
            _saveFailed.emit(Unit)
        }
    }

}

sealed class AddUserEvent {
    class SaveClicked(
        val name: String,
        val conversionType: Int,
        val conversionValue: Int,
        val requiresPassword: Boolean,
        val goal: Int,
        val goalName: String,
        val isAdult: Boolean
    ) : AddUserEvent()
    object MoneyToggled: AddUserEvent()
    object RewardToggled: AddUserEvent()
    object AddImageClicked: AddUserEvent()
    object RequiresPasswordToggled : AddUserEvent()
    object ParentToggled: AddUserEvent()
}