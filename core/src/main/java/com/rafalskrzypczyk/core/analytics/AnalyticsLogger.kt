package com.rafalskrzypczyk.core.analytics

/**
 * Jedyny punkt wysyłki danych analitycznych. Żaden moduł nie woła dostawcy (Firebase) bezpośrednio.
 *
 * Sterowanie dostawcą (zgody, przełącznik zbierania, czyszczenie danych) celowo tu nie należy —
 * jest w [AnalyticsControls], którego jedynym konsumentem jest [AnalyticsConsentManager]. Dzięki
 * temu wstrzykiwana implementacja może być owinięta bramką zgody ([ConsentGatedAnalyticsLogger]).
 */
interface AnalyticsLogger {
    fun log(event: AnalyticsEvent)

    fun setUserProperty(property: AnalyticsUserProperty, value: String)
}
