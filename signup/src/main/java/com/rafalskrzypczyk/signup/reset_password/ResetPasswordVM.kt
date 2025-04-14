package com.rafalskrzypczyk.signup.reset_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.core.api_response.Response
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
class ResetPasswordVM @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow<AuthenticationState>(AuthenticationState())
    val state: StateFlow<AuthenticationState> = _state.asStateFlow()

    fun onEvent(event: ResetPasswordUIEvents) {
        when(event) {
            ResetPasswordUIEvents.ClearError -> _state.update { it.copy(error = null) }
            is ResetPasswordUIEvents.SendResetPasswordEmail -> sendResetPasswordEmail(event.email)
        }
    }

    private fun sendResetPasswordEmail(email: String) {
        viewModelScope.launch {
            authRepository.sendPasswordResetToEmail(email).collectLatest { response ->
                when(response) {
                    is Response.Error -> _state.update {
                        it.copy(
                            isLoading = false,
                            error = response.error
                        )
                    }
                    is Response.Success -> _state.update {
                        it.copy(
                            isSuccess = true,
                            isLoading = false
                        ) }
                    Response.Loading -> _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}