package com.rafalskrzypczyk.core.analytics

import com.rafalskrzypczyk.core.error.CrashReporter
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Jedyne miejsce, które decyduje o zbieraniu danych: właściciel zapisanej decyzji użytkownika
 * i jedyny pisarz zgód po stronie dostawcy.
 *
 * Model jest opt-in — dopóki decyzja to [AnalyticsConsentState.UNDECIDED], zbieranie jest
 * wyłączone, bramka zamknięta, a zebrane wcześniej dane czyszczone.
 *
 * Dwa źródła zgody są rozłączne i dlatego dają się scalić bez konfliktu: ten menedżer jest
 * właścicielem `analytics_storage`, a formularz UMP — trzech flag reklamowych, które trafiają tu
 * przez [updateAdConsent]. Wcześniej oba pisały komplet czterech wartości, więc callback UMP
 * kasował decyzję użytkownika przy każdym starcie MainActivity.
 */
@Singleton
class AnalyticsConsentManager @Inject constructor(
    private val sharedPreferences: SharedPreferencesApi,
    private val controls: AnalyticsControls,
    private val crashReporter: CrashReporter,
    private val collectionGate: AnalyticsCollectionGate,
) {
    private val _state = MutableStateFlow(sharedPreferences.getAnalyticsConsent())
    val state: StateFlow<AnalyticsConsentState> = _state.asStateFlow()

    /** Ostatni znany stan zgód reklamowych. Zmieniany z wątku głównego przez callback UMP. */
    @Volatile
    private var adConsent: AdConsent = AdConsent.denied()

    /**
     * Wołane przy starcie aplikacji, przed czymkolwiek innym. [adConsent] to zasiew z zapisanych
     * ciągów TCF — bez niego użytkownik, który już przeszedł formularz UMP, dostawałby odmowę
     * zgód reklamowych przy każdym starcie, aż do asynchronicznego callbacku.
     *
     * Idempotentne: w jednym cyklu życia procesu leci co najmniej dwa razy.
     */
    fun apply(adConsent: AdConsent = AdConsent.denied()) {
        this.adConsent = adConsent
        applyCurrentState()
    }

    /** Zgoda z ekranu zgody albo z przełącznika w ustawieniach. */
    fun grant() = updateDecision(AnalyticsConsentState.GRANTED)

    /** Odmowa z ekranu zgody. */
    fun deny() = updateDecision(AnalyticsConsentState.DENIED)

    /**
     * Wycofanie zgody z ustawień. Osobna nazwa dla czytelności wywołań — skutek jest ten sam
     * co [deny], bo czyszczenie danych robi [applyCurrentState] dla każdego stanu poza
     * [AnalyticsConsentState.GRANTED], a nie sama ta metoda.
     */
    fun withdraw() = updateDecision(AnalyticsConsentState.DENIED)

    /** Wyłącznie dla opcji deweloperskich: przywraca stan sprzed pierwszego pytania. */
    fun reset() = updateDecision(AnalyticsConsentState.UNDECIDED)

    /**
     * Callback UMP. Przekazuje wyłącznie flagi reklamowe — decyzja o analityce należy do
     * użytkownika i jest odczytywana tutaj, a nie zapamiętywana w konstruktorze, żeby zgoda
     * udzielona pomiędzy odczytem a zapisem nie przepadła.
     */
    fun updateAdConsent(adConsent: AdConsent) {
        this.adConsent = adConsent
        applyCurrentState()
    }

    private fun updateDecision(state: AnalyticsConsentState) {
        sharedPreferences.setAnalyticsConsent(state)
        // Bramka i zgody dostawcy musza byc ustawione, ZANIM ktokolwiek zobaczy nowy stan:
        // kolektor `state` na Dispatchers.Main.immediate wznawia sie synchronicznie w setterze
        // ponizej, wiec przy odwrotnej kolejnosci wlasciwosci uzytkownika trafialyby w bramke
        // jeszcze zamknieta.
        applyCurrentState(state)
        _state.value = state
    }

    private fun applyCurrentState(decision: AnalyticsConsentState = _state.value) {
        val isGranted = decision == AnalyticsConsentState.GRANTED
        val ads = adConsent

        collectionGate.isOpen = isGranted

        controls.setConsent(
            AnalyticsConsent(
                analyticsStorage = isGranted,
                adStorage = ads.adStorage,
                adUserData = ads.adUserData,
                adPersonalization = ads.adPersonalization,
            )
        )
        controls.setCollectionEnabled(isGranted)
        crashReporter.setCollectionEnabled(isGranted)

        if (!isGranted) {
            // Także przy starcie bez zgody: zamyka okno, w którym SDK zdążyło coś zebrać, zanim
            // wykonał się nasz kod (Firebase wstaje z ContentProvidera przed Application.onCreate).
            controls.resetAnalyticsData()
            crashReporter.deleteUnsentReports()
        }
    }
}
