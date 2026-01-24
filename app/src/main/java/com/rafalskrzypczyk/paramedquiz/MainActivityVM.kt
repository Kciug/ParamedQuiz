package com.rafalskrzypczyk.paramedquiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import com.rafalskrzypczyk.firestore.domain.models.TermsOfServiceStatus
import com.rafalskrzypczyk.firestore.domain.use_cases.GetTermsOfServiceUC
import com.rafalskrzypczyk.paramedquiz.navigation.MainMenu
import com.rafalskrzypczyk.paramedquiz.navigation.Onboarding
import com.rafalskrzypczyk.paramedquiz.navigation.TermsOfService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityVM @Inject constructor(
    private val sharedPrefs: SharedPreferencesApi,
    private val getTermsOfServiceUC: GetTermsOfServiceUC
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
            getTermsOfServiceUC().collect { status ->
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
                    TermsOfServiceStatus.Loading -> { /* Keep waiting */ }
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

            getTermsOfServiceUC().collect { status ->
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
                    TermsOfServiceStatus.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }
}

data class MainActivityState(
    val startDestination: Any? = null,
    val isLoading: Boolean = true
)
