package com.rafalskrzypczyk.home_screen.presentation.home_screen.user_settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.user_management.UserData
import com.rafalskrzypczyk.core.user_management.UserManager
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.home.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSettingsVM @Inject constructor(
    private val authRepository: AuthRepository,
    private val userManager: UserManager,
    private val resourceProvider: ResourceProvider
) : ViewModel() {
    private val _state = MutableStateFlow(UserSettingsState())
    val state: StateFlow<UserSettingsState> = _state.asStateFlow()

    private var userData: UserData? = null

    init {
        getUserData()
    }

    fun onEvent(event: UserSettingsUIEvents) {
        when(event) {
            is UserSettingsUIEvents.ChangePassword -> changePassword(event.oldPassword, event.newPassword, event.newPasswordRepeat)
            is UserSettingsUIEvents.ChangeUsername -> changeUserName(event.newUsername)
            is UserSettingsUIEvents.DeleteAccount -> deleteAccount(event.password)
            UserSettingsUIEvents.SignOut -> authRepository.signOut()
            UserSettingsUIEvents.ClearState -> _state.update { it.copy(responseState = ResponseState.Idle) }
        }
    }

    private fun getUserData() {
        userManager.getCurrentLoggedUser()?.let { userData ->
            this.userData = userData
            _state.update {
                it.copy(
                    userName = userData.name,
                    userEmail = userData.email
                )
            }
        }

        if(userData == null) _state.update {
            it.copy(responseState = ResponseState.Error(resourceProvider.getString(R.string.error_user_data_not_found)))
        }
    }

    private suspend fun performWithReauthentication(password: String, onAuthenticated: suspend () -> Unit) {
        authRepository.reauthenticate(userData!!.email, password).collectLatest { response ->
            when(response) {
                is Response.Error -> _state.update { it.copy(responseState = ResponseState.Error(response.error)) }
                Response.Loading -> _state.update { it.copy(responseState = ResponseState.Loading) }
                is Response.Success -> { onAuthenticated() }
            }
        }
    }

    private fun changePassword(oldPassword: String, newPassword: String, newPasswordRepeat: String) {
        if(newPassword != newPasswordRepeat) {
            _state.update {
                it.copy(passwordValidationMessage = resourceProvider.getString(R.string.validation_password_not_match))
            }
            return
        }

        if(newPassword == oldPassword) {
            _state.update {
                it.copy(passwordValidationMessage = resourceProvider.getString(R.string.validation_password_the_same)) }
            return
        }

        _state.update { it.copy(passwordValidationMessage = null) }

        viewModelScope.launch {
            performWithReauthentication(oldPassword) {
                performPasswordChange(newPassword)
            }
        }
    }

    private suspend fun performPasswordChange(newPassword: String) {
        authRepository.changePassword(newPassword).collectLatest { response ->
            when(response) {
                is Response.Error -> _state.update { it.copy(responseState = ResponseState.Error(response.error)) }
                Response.Loading -> _state.update { it.copy(responseState = ResponseState.Loading) }
                is Response.Success ->  _state.update { it.copy(responseState = ResponseState.Success) }
            }
        }
    }

    private fun changeUserName(newUsername: String) {
        userData?.let {
            if (it.name == newUsername) {
                _state.update { it.copy(usernameValidationMessage = resourceProvider.getString(R.string.validation_username_the_same)) }
                return
            }

            _state.update { it.copy(usernameValidationMessage = null) }

            viewModelScope.launch {
                performUserNameChange(newUsername)
            }
        }
    }

    private suspend fun performUserNameChange(newUsername: String) {
        authRepository.changeUserName(newUsername).collectLatest { response ->
            when (response) {
                is Response.Error -> _state.update { it.copy(responseState = ResponseState.Error(response.error)) }
                Response.Loading -> _state.update { it.copy(responseState = ResponseState.Loading) }
                is Response.Success -> {
                    _state.update {
                        userData = response.data
                        it.copy(
                            responseState = ResponseState.Success,
                            userName = userData!!.name,
                        )
                    }
                }
            }
        }
    }

    private fun deleteAccount(password: String) {
        viewModelScope.launch {
            performWithReauthentication(password) {
                authRepository.deleteUser().collectLatest { response ->
                    when(response) {
                        is Response.Error -> _state.update { it.copy(responseState = ResponseState.Error(response.error)) }
                        Response.Loading -> _state.update { it.copy(responseState = ResponseState.Loading) }
                        is Response.Success -> _state.update {
                            it.copy(
                                responseState = ResponseState.Success,
                                successConfirmAction = UserSettingsConfirmAction.NAVIGATE_OUT
                            )
                        }
                    }
                }
            }
        }
    }
}