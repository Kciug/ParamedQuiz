package com.rafalskrzypczyk.signup.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.core.api_response.Response
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
        if(event is LoginUIEvents.LoginWithCredentials) {
            loginWithCredentials(event.email, event.password)
        }
    }

    private fun loginWithCredentials(email: String, password: String) {
        viewModelScope.launch {
            authRepository.loginWithEmailAndPassword(email, password).collectLatest {
                when(it) {
                    is Response.Error -> _state.update { it.copy(error = it.error) }
                    Response.Loading -> _state.update { it.copy(isLoading = true) }
                    is Response.Success -> _state.update { it.copy(authenticationSuccessfull = true) }
                }
            }
        }
    }
}