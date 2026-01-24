package com.rafalskrzypczyk.paramedquiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import com.rafalskrzypczyk.firestore.domain.models.TermsOfServiceStatus
import com.rafalskrzypczyk.firestore.domain.use_cases.ListenTermsOfServiceUpdatesUC
import com.rafalskrzypczyk.paramedquiz.navigation.MainMenu
import com.rafalskrzypczyk.paramedquiz.navigation.Onboarding
import com.rafalskrzypczyk.paramedquiz.navigation.TermsOfService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@HiltViewModel
class MainActivityVM @Inject constructor(
    private val sharedPrefs: SharedPreferencesApi,
    private val listenTermsOfServiceUpdatesUC: ListenTermsOfServiceUpdatesUC
) : ViewModel() {

    private val _state = MutableStateFlow(MainActivityState())
    val state = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Any>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        checkInitialStatus()
    }

    fun onEvent(event: MainActivityUIEvents) {
        when (event) {
            MainActivityUIEvents.OnboardingFinished -> onOnboardingFinished()
        }
    }

    private fun onOnboardingFinished() {
        sharedPrefs.setOnboardingStatus(true)
        viewModelScope.launch {
            val status = getDefinitiveStatus()

            when (status) {
                is TermsOfServiceStatus.Accepted -> {
                    _navigationEvent.emit(MainMenu)
                }
                is TermsOfServiceStatus.NeedsAcceptance -> {
                    _navigationEvent.emit(TermsOfService)
                }
                is TermsOfServiceStatus.Error -> {
                    val destination = if (sharedPrefs.getAcceptedTermsVersion() != -1) MainMenu else TermsOfService
                    _navigationEvent.emit(destination)
                }
                else -> {
                    _navigationEvent.emit(MainMenu)
                }
            }
        }
    }

    private fun checkInitialStatus() {
        viewModelScope.launch {
            if (!sharedPrefs.getOnboardingStatus()) {
                _state.update { it.copy(startDestination = Onboarding, isLoading = false) }
                return@launch
            }

            val status = getDefinitiveStatus()

            when (status) {
                is TermsOfServiceStatus.Accepted -> {
                    _state.update { it.copy(startDestination = MainMenu, isLoading = false) }
                }
                is TermsOfServiceStatus.NeedsAcceptance -> {
                    _state.update { it.copy(startDestination = TermsOfService, isLoading = false) }
                }
                is TermsOfServiceStatus.Error -> {
                    val destination = if (sharedPrefs.getAcceptedTermsVersion() != -1) MainMenu else TermsOfService
                    _state.update { it.copy(startDestination = destination, isLoading = false) }
                }
                else -> {
                    // Fallback for timeout/null
                    val destination = if (sharedPrefs.getAcceptedTermsVersion() != -1) MainMenu else TermsOfService
                    _state.update { it.copy(startDestination = destination, isLoading = false) }
                }
            }
        }
    }

    private suspend fun getDefinitiveStatus(): TermsOfServiceStatus? {
        return withTimeoutOrNull(3000L) {
            listenTermsOfServiceUpdatesUC()
                .filter { it !is TermsOfServiceStatus.Loading }
                .first()
        }
    }
}

data class MainActivityState(
    val startDestination: Any? = null,
    val isLoading: Boolean = true
)
