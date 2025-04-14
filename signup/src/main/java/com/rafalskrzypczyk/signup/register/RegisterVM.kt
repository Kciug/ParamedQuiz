package com.rafalskrzypczyk.signup.register

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
class RegisterVM @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow<AuthenticationState>(AuthenticationState())
    val state: StateFlow<AuthenticationState> = _state.asStateFlow()

    fun onEvent(event: RegisterUIEvents) {
        when (event) {
            RegisterUIEvents.ClearError -> _state.update { it.copy(error = null) }
            is RegisterUIEvents.RegisterWithCredentials -> registerWithCredentials(event.name ,event.email, event.password)
        }
    }

    fun registerWithCredentials(name: String, email: String, password: String) {
        viewModelScope.launch {
            authRepository.registerWithEmailAndPassword(email, password, name).collectLatest { response ->
                when(response) {
                    is Response.Error -> _state.update { it.copy(
                        error = response.error,
                        isLoading = false
                    ) }
                    Response.Loading -> _state.update { it.copy(isLoading = true) }
                    is Response.Success -> _state.update { it.copy(isSuccess = true) }
                }
            }
        }
    }
}