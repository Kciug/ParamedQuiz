package com.rafalskrzypczyk.core.analytics

/**
 * Dekorator odcinający wysyłkę, dopóki [gate] jest zamknięta.
 *
 * Owija właściwą implementację w [com.rafalskrzypczyk.analytics.di.AnalyticsModule]. Nie
 * implementuje [AnalyticsControls] — sterowanie dostawcą musi omijać bramkę, inaczej nie dałoby
 * się jej otworzyć.
 */
class ConsentGatedAnalyticsLogger(
    private val delegate: AnalyticsLogger,
    private val gate: AnalyticsCollectionGate,
) : AnalyticsLogger {

    override fun log(event: AnalyticsEvent) {
        if (!gate.isOpen) return
        delegate.log(event)
    }

    override fun setUserProperty(property: AnalyticsUserProperty, value: String) {
        if (!gate.isOpen) return
        delegate.setUserProperty(property, value)
    }
}
