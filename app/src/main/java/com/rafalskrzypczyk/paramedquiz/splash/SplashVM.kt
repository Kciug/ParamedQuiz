package com.rafalskrzypczyk.paramedquiz.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import com.rafalskrzypczyk.firestore.domain.models.TermsOfServiceStatus
import com.rafalskrzypczyk.firestore.domain.use_cases.GetTermsOfServiceUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashVM @Inject constructor(
    private val sharedPrefs: SharedPreferencesApi,
    private val getTermsOfServiceUC: GetTermsOfServiceUC
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<SplashNavigationEvent>(replay = 1)
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        checkStatus()
    }

    private fun checkStatus() {
        viewModelScope.launch {
            if (!sharedPrefs.getOnboardingStatus()) {
                _navigationEvent.emit(SplashNavigationEvent.NavigateToOnboarding)
                return@launch
            }

            getTermsOfServiceUC().collect { status ->
                when (status) {
                    is TermsOfServiceStatus.Accepted -> {
                        _navigationEvent.emit(SplashNavigationEvent.NavigateToMainMenu)
                    }
                    is TermsOfServiceStatus.NeedsAcceptance -> {
                        _navigationEvent.emit(SplashNavigationEvent.NavigateToTermsOfService)
                    }
                    is TermsOfServiceStatus.Error -> {
                        if (sharedPrefs.getAcceptedTermsVersion() != -1) {
                            _navigationEvent.emit(SplashNavigationEvent.NavigateToMainMenu)
                        } else {
                            _navigationEvent.emit(SplashNavigationEvent.NavigateToTermsOfService)
                        }
                    }
                    TermsOfServiceStatus.Loading -> { /* Keep waiting */ }
                }
            }
        }
    }
}

sealed interface SplashNavigationEvent {
    data object NavigateToOnboarding : SplashNavigationEvent
    data object NavigateToTermsOfService : SplashNavigationEvent
    data object NavigateToMainMenu : SplashNavigationEvent
}
