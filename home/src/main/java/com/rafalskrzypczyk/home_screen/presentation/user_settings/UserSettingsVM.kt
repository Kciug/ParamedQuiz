package com.rafalskrzypczyk.home_screen.presentation.user_settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.home_screen.domain.use_cases.UserSettingsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSettingsVM @Inject constructor(
    private val useCases: UserSettingsUseCases
) : ViewModel() {
    private val _state = MutableStateFlow(UserSettingsState())
    val state = _state.asStateFlow()

    init {
        loadUserData()
    }

    fun onEvent(event: UserSettingsUIEvents) {
        when(event) {
            is UserSettingsUIEvents.ChangePassword -> changePassword(event.oldPassword, event.newPassword, event.newPasswordRepeat)
            is UserSettingsUIEvents.ChangeUsername -> changeUsername(event.newUsername)
            UserSettingsUIEvents.ClearState -> _state.update { it.copy(responseState = ResponseState.Idle, passwordValidationMessage = null, usernameValidationMessage = null) }
            is UserSettingsUIEvents.DeleteAccount -> deleteAccount(event.password)
            is UserSettingsUIEvents.DeleteAccountForProvider -> deleteAccountForProvider(event.context)
            UserSettingsUIEvents.SignOut -> signOut()
            is UserSettingsUIEvents.ToggleChangePasswordDialog -> _state.update { it.copy(showChangePasswordDialog = event.show) }
            is UserSettingsUIEvents.ToggleChangeUsernameDialog -> _state.update { it.copy(showChangeUsernameDialog = event.show) }
            is UserSettingsUIEvents.ToggleDeleteAccountDialog -> _state.update { it.copy(showDeleteAccountDialog = event.show) }
            is UserSettingsUIEvents.ToggleDeleteProgressDialog -> _state.update { it.copy(showDeleteProgressDialog = event.show) }
            UserSettingsUIEvents.OnSuccessToastShown -> _state.update { it.copy(showSuccessToast = false) }
            UserSettingsUIEvents.DeleteProgress -> deleteProgress()
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            useCases.getUserData().collectLatest { response ->
                when(response) {
                    is Response.Error -> _state.update { it.copy(responseState = ResponseState.Error(response.error)) }
                    Response.Loading -> _state.update { it.copy(responseState = ResponseState.Loading) }
                    is Response.Success -> {
                        val userData = response.data
                        if (userData != null) {
                            _state.update {
                                it.copy(
                                    responseState = ResponseState.Idle,
                                    userName = userData.name,
                                    userEmail = userData.email,
                                    accountType = userData.authenticationMethod,
                                    isAnonymous = false
                                )
                            }
                        } else {
                            _state.update {
                                it.copy(
                                    responseState = ResponseState.Idle,
                                    isAnonymous = true,
                                    userName = "",
                                    userEmail = ""
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun changeUsername(newUsername: String) {
        val validationResult = useCases.validateUsername(state.value.userName, newUsername)
        if(validationResult != null) {
            _state.update { it.copy(usernameValidationMessage = validationResult) }
            return
        }

        viewModelScope.launch {
            useCases.updateUsername(newUsername).collectLatest { response ->
                handleResponse(response, UserSettingsConfirmAction.CLEAR_STATE)
                if (response is Response.Success) {
                    _state.update { it.copy(showChangeUsernameDialog = false, showSuccessToast = true) }
                    loadUserData()
                }
            }
        }
    }

    private fun changePassword(oldPassword: String, newPassword: String, newPasswordRepeat: String) {
        val validationResult = useCases.validatePasswordChange(oldPassword, newPassword, newPasswordRepeat)
        if(validationResult != null) {
            _state.update { it.copy(passwordValidationMessage = validationResult) }
            return
        }

        viewModelScope.launch {
            useCases.updatePassword(oldPassword, newPassword).collectLatest { response ->
                handleResponse(response, UserSettingsConfirmAction.CLEAR_STATE)
                if (response is Response.Success) {
                    _state.update { it.copy(showChangePasswordDialog = false, showSuccessToast = true) }
                }
            }
        }
    }

    private fun deleteAccount(password: String) {
        viewModelScope.launch {
            useCases.deleteAccount(password).collectLatest { response ->
                handleResponse(response, UserSettingsConfirmAction.NAVIGATE_OUT)
                if (response is Response.Success) {
                    _state.update { it.copy(showDeleteAccountDialog = false) }
                }
            }
        }
    }

    private fun deleteAccountForProvider(context: Context) {
        viewModelScope.launch {
            useCases.deleteAccountForProvider(context).collectLatest { response ->
                handleResponse(response, UserSettingsConfirmAction.NAVIGATE_OUT)
                if (response is Response.Success) {
                    _state.update { it.copy(showDeleteAccountDialog = false) }
                }
            }
        }
    }

    private fun deleteProgress() {
        viewModelScope.launch {
            useCases.deleteProgress().collectLatest { response ->
                handleResponse(response, UserSettingsConfirmAction.CLEAR_STATE)
                if (response is Response.Success) {
                    _state.update { it.copy(showDeleteProgressDialog = false, showSuccessToast = true) }
                }
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            useCases.signOut()
        }
    }

    private fun handleResponse(response: Response<Unit>, successAction: UserSettingsConfirmAction) {
        when(response) {
            is Response.Error -> _state.update { it.copy(responseState = ResponseState.Error(response.error)) }
            Response.Loading -> _state.update { it.copy(responseState = ResponseState.Loading) }
            is Response.Success -> _state.update {
                it.copy(
                    responseState = ResponseState.Success,
                    successConfirmAction = successAction
                )
            }
        }
    }
}