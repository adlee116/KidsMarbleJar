package com.example.kidsmarblejar.presentation.marbleJar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidsmarblejar.domain.GetUserUseCase
import com.example.kidsmarblejar.domain.UpdateUserUseCase
import com.example.kidsmarblejar.presentation.addUser.RewardType
import com.example.kidsmarblejar.presentation.home.UserEntity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MarbleJarViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase
) : ViewModel() {

    private val _marbleJarState = MutableStateFlow<MarbleJarState>(MarbleJarState.Loading)
    val marbleJarState = _marbleJarState.asStateFlow()

    private val _userFetchFailed = MutableSharedFlow<Unit>()
    val userFetchFailed = _userFetchFailed.asSharedFlow()

    private val _userUpdateFailed = MutableSharedFlow<Unit>()
    val userUpdateFailed = _userUpdateFailed.asSharedFlow()

    lateinit var userEntity: UserEntity

    fun process(event: MarbleJarEvent) {
        when (event) {
            is MarbleJarEvent.Initialise -> getUser(event.id)
            is MarbleJarEvent.AddMarbles -> addMarbles()
        }
    }

    private fun addMarbles() {
        _marbleJarState.value = MarbleJarState.Loading
        val addMarblesParams = UpdateUserUseCase.Params(
            userEntity = userEntity,
            marbles = userEntity.marbles + 1
        )
        updateUserUseCase.invoke(viewModelScope, addMarblesParams) { result ->
            result.result(
                onSuccess = {
                    userEntity = it
                    _marbleJarState.value = MarbleJarState.Reading(
                        it,
                        getMarbleList(it.marbles, intToRewardType(it.conversionType), it.goal)
                    )
                },
                onFailure = { emitUserUpdateFail() }
            )
        }
    }

    private fun getUser(userId: Int) {
        getUserUseCase.invoke(viewModelScope, userId) { result ->
            result.result(
                onSuccess = {
                    userEntity = it
                    _marbleJarState.value = MarbleJarState.Reading(
                        it,
                        getMarbleList(it.marbles, intToRewardType(it.conversionType), it.goal)
                    )
                },
                onFailure = { emitUserFetchFail() }
            )
        }
    }

    private fun getMarbleList(marbles: Int, conversionType: RewardType, marblesRequired: Int): List<Marble> {
        val marblesList = mutableListOf<Marble>()
        repeat(marbles) { marblesList.add(Marble(true)) }
        if (conversionType == RewardType.REWARD) {
            repeat(marblesRequired - marbles) { marblesList.add(Marble(false)) }
        }
        return marblesList
    }

    private fun intToRewardType(intValue: Int): RewardType {
        return if (intValue == 1) {
            RewardType.MONEY
        } else {
            RewardType.REWARD
        }
    }

    private fun emitUserFetchFail() {
        viewModelScope.launch { _userFetchFailed.emit(Unit) }
    }

    private fun emitUserUpdateFail() {
        viewModelScope.launch { _userUpdateFailed.emit(Unit) }
    }

}

sealed class MarbleJarState {
    object Loading : MarbleJarState()
    class Reading(val user: UserEntity, val marbles: List<Marble>) : MarbleJarState()
}

sealed class MarbleJarEvent {
    class Initialise(val id: Int) : MarbleJarEvent()
    object AddMarbles : MarbleJarEvent()
}
