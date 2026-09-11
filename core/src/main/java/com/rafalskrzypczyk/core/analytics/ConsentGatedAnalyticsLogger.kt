package com.rafalskrzypczyk.core.analytics

/**
 * Dekorator odcinający wysyłkę, dopóki [gate] jest zamknięta.
 *
 * Owija właściwą implementację w [com.rafalskrzypczyk.analytics.di.AnalyticsModule]. Nie
 * implementuje [AnalyticsControls] — sterowanie dostawcą musi omijać bramkę, inaczej nie dałoby
 * się jej otworzyć.
 *
 * [recent] jest zasilany dopiero **za** bramką — podgląd w opcjach deweloperskich ma pokazywać
 * to, co faktycznie wyszło.
 */
class ConsentGatedAnalyticsLogger(
    private val delegate: AnalyticsLogger,
    private val gate: AnalyticsCollectionGate,
    private val recent: RecentAnalyticsEvents? = null,
) : AnalyticsLogger {

    override fun log(event: AnalyticsEvent) {
        if (!gate.isOpen) return
        recent?.record(event)
        delegate.log(event)
    }

    override fun setUserProperty(property: AnalyticsUserProperty, value: String) {
        if (!gate.isOpen) return
        recent?.record(property, value)
        delegate.setUserProperty(property, value)
    }
}
