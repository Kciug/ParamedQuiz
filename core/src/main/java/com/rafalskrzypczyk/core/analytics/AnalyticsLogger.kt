package com.rafalskrzypczyk.core.analytics

/**
 * Jedyny punkt wysyłki danych analitycznych. Żaden moduł nie woła dostawcy (Firebase) bezpośrednio.
 *
 * Implementacja domyślna ([LogcatAnalyticsLogger]) żyje w `core`, produkcyjna w module `:analytics`.
 * Wiązanie jest wybierane w `AnalyticsModule` po typie builda — analogicznie do [ErrorLogger].
 */
interface AnalyticsLogger {
    fun log(event: AnalyticsEvent)

    fun setUserProperty(property: AnalyticsUserProperty, value: String)

    /** Propagacja zgód (UMP/TCF) do dostawcy. Idempotentne — wołane przy każdym ustaleniu zgody. */
    fun setConsent(consent: AnalyticsConsent)
}
