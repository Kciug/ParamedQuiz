package com.rafalskrzypczyk.core.analytics

/**
 * Sterowanie dostawcą analityki, oddzielone od wysyłki zdarzeń ([AnalyticsLogger]).
 *
 * Podział jest celowy: jedynym konsumentem tego interfejsu jest [AnalyticsConsentManager], dzięki
 * czemu logger może być owinięty bramką zgody bez cyklu zależności.
 */
interface AnalyticsControls {
    /** Propagacja zgód do dostawcy (Consent Mode). Idempotentne. */
    fun setConsent(consent: AnalyticsConsent)

    /** Globalny przełącznik zbierania. Wartość jest trwała po stronie SDK. */
    fun setCollectionEnabled(enabled: Boolean)

    /** Czyści zebrane dane i identyfikator instancji aplikacji. */
    fun resetAnalyticsData()
}

/**
 * Implementacja dostawcy: wysylka i sterowanie w jednym obiekcie. Wystawiana w grafie DI pod
 * oboma interfejsami, zeby menedzer zgody i owiniety bramka logger operowaly na tej samej instancji.
 */
interface AnalyticsBackend : AnalyticsLogger, AnalyticsControls
