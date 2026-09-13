package com.rafalskrzypczyk.signup.login

import android.content.Context
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
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<AuthenticationState>(AuthenticationState())
    val state: StateFlow<AuthenticationState> = _state.asStateFlow()

    fun onEvent(event: LoginUIEvents) {
        when(event) {
            LoginUIEvents.ClearError -> _state.update { it.copy(error = null) }
            is LoginUIEvents.LoginWithCredentials -> loginWithCredentials(event.email, event.password)
            is LoginUIEvents.LoginWithGoogle -> loginWithGoogle(event.context)
        }
    }

    private fun loginWithCredentials(email: String, password: String) {
        viewModelScope.launch {
            authRepository.loginWithEmailAndPassword(email, password).collectLatest { response ->
                handleLoginResponse(response)
            }
        }
    }

    /**
     * Anulowanie wyboru konta kończy flow bez [Response.Error] i bez [Response.Success], a stan
     * ładowania trwa od kliknięcia — [onCompletion] zdejmuje go w tym jednym przypadku.
     */
    private fun loginWithGoogle(context: Context) {
        viewModelScope.launch {
            authRepository.signInWithGoogle(context)
                .onCompletion { clearLoadingWithoutResult() }
                .collectLatest { response ->
                    handleLoginResponse(response)
                }
        }
    }

    private fun clearLoadingWithoutResult() {
        _state.update { if (it.isSuccess) it else it.copy(isLoading = false) }
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