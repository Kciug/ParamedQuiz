package com.rafalskrzypczyk.home_screen.presentation.onboarding

import androidx.lifecycle.ViewModel
import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.core.user_management.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class OnboardingVM @Inject constructor(
    private val authRepository: AuthRepository,
    private val userManager: UserManager,
): ViewModel() {
    private val _state = MutableStateFlow(OnboardingState())
    val state = _state.asStateFlow()

    fun onEvent(event: OnboardingUIEvents) {
        when(event) {
            OnboardingUIEvents.CheckIsLogged -> checkIsLogged()
        }
    }

    private fun checkIsLogged() {
        val isLogged = authRepository.isUserLoggedIn()
        _state.update { it.copy(isLogged = isLogged) }

        if(isLogged){
            userManager.getCurrentLoggedUser()?.let { user ->
                _state.update { it.copy(
                    userName = user.name,
                    userEmail = user.email
                ) }
            }
        }
    }
}