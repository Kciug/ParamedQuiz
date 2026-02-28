package com.rafalskrzypczyk.home_screen.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import com.rafalskrzypczyk.core.user_management.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingVM @Inject constructor(
    private val authRepository: AuthRepository,
    private val userManager: UserManager,
    private val premiumStatusProvider: PremiumStatusProvider
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

        viewModelScope.launch {
            premiumStatusProvider.ownedProductIds.collectLatest { ownedIds ->
                val isPremium = ownedIds.contains(BillingIds.ID_FULL_PACKAGE)
                _state.update { it.copy(isPremium = isPremium) }
            }
        }
    }
}