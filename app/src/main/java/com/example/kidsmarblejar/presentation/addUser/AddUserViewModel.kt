package com.example.kidsmarblejar.presentation.addUser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kidsmarblejar.domain.GetUserUseCase
import com.example.kidsmarblejar.domain.NewUser
import com.example.kidsmarblejar.domain.SaveUserUseCase
import com.example.kidsmarblejar.domain.UpdateUserUseCase
import com.example.kidsmarblejar.presentation.home.UserEntity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddUserViewModel(
    private val saveUserUseCase: SaveUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase
) : ViewModel() {

    var userEntity: UserEntity? = null

    private val _saved = MutableSharedFlow<Unit>()
    val saved = _saved.asSharedFlow()

    private val _saveFailed = MutableSharedFlow<AddUserError>()
    val saveFailed = _saveFailed.asSharedFlow()

    private val _getUserFailed = MutableSharedFlow<Unit>()
    val getUserFailed = _getUserFailed.asSharedFlow()

    private val _addUserState = MutableStateFlow<AddUserState>(AddUserState.Loading)
    val addUserState = _addUserState.asStateFlow()

    var newUser: NewUser = NewUser()

    fun process(addUserEvent: AddUserEvent) {
        when (addUserEvent) {
            AddUserEvent.Initialise -> emitChanges()
            is AddUserEvent.GetUser -> setData(addUserEvent.userId)
            is AddUserEvent.SaveAdultClicked -> saveAdult(addUserEvent.name)
            is AddUserEvent.SaveMoneyClicked -> saveMoneyChild(addUserEvent.name, addUserEvent.marbleValue)
            is AddUserEvent.SaveRewardClicked -> saveRewardChild(addUserEvent.name, addUserEvent.rewardName, addUserEvent.marblesRequired)
            is AddUserEvent.AddedImage -> updateUserPhoto(addUserEvent.image)
            AddUserEvent.MoneyToggled -> emitMoneySelected(true)
            AddUserEvent.RewardToggled -> emitMoneySelected(false)
            is AddUserEvent.ParentToggled -> parentToggled(addUserEvent.toggled)
            is AddUserEvent.RequiresPasswordToggled -> requiresPasswordToggled(addUserEvent.toggled)
        }
    }

    private fun intToRewardType(intValue: Int): RewardType {
        return if (intValue == 1) {
            RewardType.MONEY
        } else {
            RewardType.REWARD
        }
    }

    private fun setData(id: Int) {
        getUserUseCase.invoke(viewModelScope, id) { result ->
            result.result(
                onSuccess = {
                    newUser.name = it.name
                    newUser.isAdult = it.isAdult
                    newUser.rewardType = intToRewardType(it.conversionType)
                    newUser.image = it.image
                    newUser.requiresPassword = it.requiresPassword
                    newUser.marbleConversionRate = it.conversionValue
                    newUser.rewardName = it.goalName
                    newUser.marblesRequired = it.goal
                    emitChanges()
                },
                onFailure = {
                    emitGetUserFailed()
                }
            )
        }
    }

    private fun parentToggled(toggle: Boolean) {
        newUser.isAdult = toggle
        emitChanges()
    }

    private fun requiresPasswordToggled(toggle: Boolean) {
        newUser.requiresPassword = toggle
    }

    private fun updateUserPhoto(image: String) {
        newUser.image = image
    }

    private fun saveAdult(name: String) {
        newUser.name = name
        if (validateUser(newUser).first) {
            userEntity?.let {
                updateUser(it, newUser)
            } ?: run {
                saveNewUser(newUser)
            }
        }
    }

    private fun saveNewUser(newUser: NewUser) {
        saveUserUseCase.invoke(viewModelScope, newUser) { result ->
            result.result(
                onSuccess = { emitSaved() },
                onFailure = { emitFailed(AddUserError.GeneralStringError(it.message ?: "")) }
            )
        }
    }

    private fun updateUser(oldUser: UserEntity, newUser: NewUser) {
        updateUserUseCase.invoke(
            viewModelScope, UpdateUserUseCase.Params(
                oldUser,
                name = newUser.name,
                image = newUser.image,
                marbles = oldUser.marbles,
                requiresPassword = newUser.requiresPassword,
                isAdult = newUser.isAdult,
                conversionType = newUser.rewardType.rewardType,
                conversionValue = newUser.marbleConversionRate,
                goalName = newUser.rewardName,
                goal = newUser.marblesRequired
            )
        ) { result ->
            result.result(
                onSuccess = { emitSaved() },
                onFailure = { emitFailed(AddUserError.GeneralStringError(it.message ?: "")) }
            )
        }
    }

    private fun saveRewardChild(name: String, rewardName: String, marblesRequired: Int) {
        newUser.name = name
        newUser.rewardName = rewardName
        newUser.marblesRequired = marblesRequired
        if (validateUser(newUser).first) {
            userEntity?.let { updateUser(it, newUser) } ?: run { saveNewUser(newUser) }
        }
    }

    private fun saveMoneyChild(name: String, marbleValue: Double) {
        newUser.name = name
        newUser.marbleConversionRate = marbleValue
        val validation = validateUser(newUser)
        if (validation.first) {
            userEntity?.let {
                updateUser(it, newUser)
            } ?: run {
                saveNewUser(newUser)
            }
        } else {
            validation.second?.let { emitFailed(it) }
        }
    }

    private fun validateUser(newUser: NewUser): Pair<Boolean, AddUserError?> {
        return when {
            newUser.isAdult -> validateAdultUser(newUser)
            else -> validateChildUser(newUser)
        }
    }

    private fun validateAdultUser(newUser: NewUser): Pair<Boolean, AddUserError?> {
        return when {
            newUser.name.isEmpty() -> Pair(false, AddUserError.UsernameEmpty)
            newUser.image.isEmpty() -> Pair(false, AddUserError.ImageEmpty)
            else -> Pair(true, null)
        }
    }

    private fun validateChildUser(newUser: NewUser): Pair<Boolean, AddUserError?> {
        return when {
            newUser.name.isEmpty() -> Pair(false, AddUserError.UsernameEmpty)
            newUser.image.isEmpty() -> Pair(false, AddUserError.ImageEmpty)
            newUser.rewardType == RewardType.MONEY && newUser.marbleConversionRate < 0 -> Pair(false, AddUserError.MarbleValueEmpty)
            newUser.rewardType == RewardType.REWARD && newUser.rewardName.isEmpty() -> Pair(false, AddUserError.RewardNameEmpty)
            newUser.rewardType == RewardType.REWARD && newUser.marblesRequired < 0 -> Pair(false, AddUserError.MarblesRequiredEmpty)
            else -> Pair(true, null)
        }

    }

    private fun emitSaved() {
        viewModelScope.launch {
            _saved.emit(Unit)
        }
    }

    private fun emitFailed(error: AddUserError) {
        viewModelScope.launch {
            _saveFailed.emit(error)
        }
    }

    private fun emitGetUserFailed() {
        viewModelScope.launch {
            _getUserFailed.emit(Unit)
        }
    }

    private fun emitMoneySelected(moneySelected: Boolean) {
        newUser.rewardType = if (moneySelected) RewardType.MONEY else RewardType.REWARD
        emitChanges()
    }

    private fun emitChanges() {
        _addUserState.value = AddUserState.Reading(newUser)
    }

}

sealed class AddUserEvent {
    object Initialise : AddUserEvent()
    class GetUser(val userId: Int) : AddUserEvent()
    class SaveRewardClicked(val name: String, val rewardName: String, val marblesRequired: Int) : AddUserEvent()
    class SaveMoneyClicked(val name: String, val marbleValue: Double) : AddUserEvent()
    class SaveAdultClicked(val name: String) : AddUserEvent()
    object MoneyToggled : AddUserEvent()
    object RewardToggled : AddUserEvent()
    class AddedImage(val image: String) : AddUserEvent()
    class RequiresPasswordToggled(val toggled: Boolean) : AddUserEvent()
    class ParentToggled(val toggled: Boolean) : AddUserEvent()
}

sealed class AddUserState {
    object Loading : AddUserState()
    class Reading(val newUser: NewUser) : AddUserState()
}

sealed class AddUserError {
    object UsernameEmpty : AddUserError()
    object ImageEmpty : AddUserError()
    object MarbleValueEmpty : AddUserError()
    object RewardNameEmpty : AddUserError()
    object MarblesRequiredEmpty : AddUserError()
    class GeneralIntError(error: Int) : AddUserError()
    class GeneralStringError(error: String) : AddUserError()
}

enum class RewardType(val rewardType: Int) {
    MONEY(rewardType = 1),
    REWARD(rewardType = 2)
}