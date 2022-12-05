package com.example.kidsmarblejar.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidsmarblejar.core.utils.justValue
import com.example.kidsmarblejar.domain.GetAllUsersUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getAllUsersUseCase: GetAllUsersUseCase
) : ViewModel() {

    private val _users = MutableSharedFlow<List<UserModel>>()
    val users = _users.asSharedFlow()

    private val _userClicked = MutableSharedFlow<Pair<Int, Boolean>>()
    val userClicked = _userClicked.asSharedFlow()

    var currentUsers: List<UserModel> = emptyList()

    fun process(homeEvent: HomeEvent) {
        when (homeEvent) {
            is HomeEvent.Initialise -> initialise()
            is HomeEvent.UserItemClicked -> userItemClicked(homeEvent.userId)
        }
    }

    private fun getAllUsers(): List<UserEntity> {
        return getAllUsersUseCase.justValue()
    }

    private fun userItemClicked(userId: Any?) {

    }

    private fun initialise() {
        val userList = mutableListOf<UserEntity>()
        userList.addAll(getAllUsers())
        val userModels = userEntityToModel(userList)
        currentUsers = userModels
        emitUsers(currentUsers)
    }

    private fun emitUsers(users: List<UserModel>) {
        viewModelScope.launch {
            _users.emit(users)
        }
    }
    private fun userEntityToModel(users: List<UserEntity>): MutableList<UserModel> {
        val userModelList = mutableListOf<UserModel>()
        userModelList.add(createAddUser())
        users.forEach {
            when {
                it.isAdult -> userModelList.add(createParent(it))
                else -> userModelList.add(createChild(it))
            }
        }
        return userModelList
    }

    private fun createAddUser(): AddUserModel {
        return AddUserModel(
            onClick = {
                userClicked(-1, false)
            }
        )
    }

    private fun userClicked(id: Int, adult: Boolean) {
        viewModelScope.launch {
            _userClicked.emit(Pair(id, adult))
        }
    }

    private fun createParent(user: UserEntity): ParentUserModel {
        return ParentUserModel(
            id = user.id,
            name = user.name,
            image = user.image,
            isParent = user.isAdult,
            onClick = {
                userClicked(user.id, user.isAdult)
            }
        )
    }

    private fun createChild(user: UserEntity): ChildUserModel {
        return ChildUserModel(
            id = user.id,
            name = user.name,
            image = user.image,
            isParent = user.isAdult,
            marbles = user.marbles,
            conversionType = user.conversionType,
            conversionValue = user.conversionValue,
            requiresPassword = user.requiresPassword,
            goal = user.goal,
            goalName = user.goalName,
            onClick = {
                userClicked(user.id, user.isAdult)
            }
        )
    }

}

sealed class HomeEvent {
    object Initialise : HomeEvent()
    class UserItemClicked(val userId: Int) : HomeEvent()
}