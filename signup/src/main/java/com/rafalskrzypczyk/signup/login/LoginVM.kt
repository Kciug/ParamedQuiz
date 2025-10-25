package com.rafalskrzypczyk.signup.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.user_management.UserData
import com.rafalskrzypczyk.signup.AuthenticationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow<AuthenticationState>(AuthenticationState())
    val state: StateFlow<AuthenticationState> = _state.asStateFlow()

    fun onEvent(event: LoginUIEvents) {
        when(event) {
            LoginUIEvents.ClearError -> _state.update { it.copy(error = null) }
            is LoginUIEvents.LoginWithCredentials -> loginWithCredentials(event.email, event.password)
            LoginUIEvents.LoginWithGoogle -> loginWithGoogle()
        }
    }

    private fun loginWithCredentials(email: String, password: String) {
        viewModelScope.launch {
            authRepository.loginWithEmailAndPassword(email, password).collectLatest { response ->
                handleLoginResponse(response)
            }
        }
    }

    private fun loginWithGoogle() {
        viewModelScope.launch {
            authRepository.signInWithGoogle().collectLatest { response ->
                handleLoginResponse(response)
            }
        }
    }

    private fun handleLoginResponse(response: Response<UserData>) {
        when(response) {
            is Response.Error -> _state.update {
                it.copy(
                    isLoading = false,
                    error = response.error
                )
            }
            Response.Loading -> _state.update { it.copy(isLoading = true) }
            is Response.Success -> _state.update { it.copy(isSuccess = true) }
        }
    }
}