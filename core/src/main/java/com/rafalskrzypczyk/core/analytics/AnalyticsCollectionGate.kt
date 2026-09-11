package com.rafalskrzypczyk.core.analytics

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Bramka zdarzeń: zamknięta, dopóki użytkownik nie wyrazi zgody.
 *
 * Wyłączenie zbierania po stronie SDK powstrzymuje wysyłkę, ale nie powstrzymuje naszego kodu
 * przed produkowaniem zdarzeń — a kontrakt cross-platform mówi, że przed zgodą **nic** nie jest
 * logowane. Bramka czyni z tego niezmiennik możliwy do przetestowania.
 *
 * Zapisuje [AnalyticsConsentManager], czyta [ConsentGatedAnalyticsLogger].
 */
@Singleton
class AnalyticsCollectionGate @Inject constructor() {
    @Volatile
    var isOpen: Boolean = false
        internal set
}
