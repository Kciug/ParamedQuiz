package com.rafalskrzypczyk.paramedquiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.analytics.AnalyticsConsentState
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import com.rafalskrzypczyk.firestore.domain.models.TermsOfServiceStatus
import com.rafalskrzypczyk.firestore.domain.use_cases.ListenTermsOfServiceUpdatesUC
import com.rafalskrzypczyk.paramedquiz.navigation.MainMenu
import com.rafalskrzypczyk.paramedquiz.navigation.Onboarding
import com.rafalskrzypczyk.paramedquiz.navigation.PrivacyConsent
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
import kotlin.time.Duration.Companion.milliseconds

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
        prefetchTermsOfService()
        checkInitialStatus()
    }

    /**
     * Rozgrzewa cache Firestore dla dokumentu regulaminu już na starcie (w trakcie onboardingu),
     * żeby po jego zakończeniu ekran regulaminu renderował się bez zauważalnego laga (spinnera).
     * `getTermsOfServiceUpdates` (decyzja) i `getTermsOfService` (treść ekranu) czytają ten sam
     * dokument, więc jeden warm-up read pokrywa oba. Błędy/brak sieci ignorujemy — to tylko warm-up.
     */
    private fun prefetchTermsOfService() {
        viewModelScope.launch {
            runCatching {
                listenTermsOfServiceUpdatesUC()
                    .filter { it !is TermsOfServiceStatus.Loading }
                    .first()
            }
        }
    }

    fun onEvent(event: MainActivityUIEvents) {
        when (event) {
            MainActivityUIEvents.OnboardingFinished -> onOnboardingFinished()
            MainActivityUIEvents.TermsAccepted -> onTermsAccepted()
        }
    }

    private fun onOnboardingFinished() {
        sharedPrefs.setOnboardingStatus(true)
        viewModelScope.launch {
            _navigationEvent.emit(resolveStartDestination())
        }
    }

    /**
     * Po akceptacji regulaminu nie odpytujemy Firestore ponownie — regulamin wlasnie zostal
     * zaakceptowany, a [resolveStartDestination] dokladaloby do 3 s oczekiwania na ekranie,
     * ktory nie ma stanu ladowania i nie jest juz przykryty splashem.
     */
    private fun onTermsAccepted() {
        viewModelScope.launch {
            _navigationEvent.emit(destinationAfterTerms())
        }
    }

    private fun checkInitialStatus() {
        viewModelScope.launch {
            if (!sharedPrefs.getOnboardingStatus()) {
                _state.update { it.copy(startDestination = Onboarding, isLoading = false) }
                return@launch
            }

            val destination = resolveStartDestination()
            _state.update { it.copy(startDestination = destination, isLoading = false) }
        }
    }

    /**
     * Wspólna reguła docelowej trasy dla [checkInitialStatus] i [onOnboardingFinished].
     *
     * Świeża instalacja (`acceptedVersion == -1`, nigdy nic nie zaakceptowano) → regulamin jest
     * obowiązkowy zawsze; nie czekamy na Firestore (short-circuit). Dla użytkownika, który już
     * kiedyś akceptował, Firestore rozstrzyga jedynie czy pojawiła się nowa wersja — a timeout/błąd
     * nie blokuje go w menu.
     */
    private suspend fun resolveStartDestination(): Any {
        if (sharedPrefs.getAcceptedTermsVersion() == -1) {
            return TermsOfService(isMandatory = true)
        }
        return when (getDefinitiveStatus()) {
            is TermsOfServiceStatus.NeedsAcceptance -> TermsOfService(isMandatory = true)
            is TermsOfServiceStatus.Accepted -> destinationAfterTerms()
            else -> destinationAfterTerms()
        }
    }

    /**
     * Bramka zgody na analitykę. Stoi tu, a nie na sciezce regulaminu, bo uzytkownik ktory
     * zaakceptowal juz obowiazujaca wersje regulaminu nigdy przez tamten ekran nie przechodzi.
     */
    private fun destinationAfterTerms(): Any =
        if (sharedPrefs.getAnalyticsConsent() == AnalyticsConsentState.UNDECIDED) {
            PrivacyConsent
        } else {
            MainMenu
        }

    private suspend fun getDefinitiveStatus(): TermsOfServiceStatus? {
        return withTimeoutOrNull(3000L.milliseconds) {
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
