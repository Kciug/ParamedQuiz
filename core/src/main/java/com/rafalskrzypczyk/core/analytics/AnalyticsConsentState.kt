package com.rafalskrzypczyk.core.analytics

/**
 * Decyzja użytkownika w sprawie analityki i diagnostyki awarii.
 *
 * Model jest opt-in: dopóki stan to [UNDECIDED], nie zbieramy niczego. Stan trwały, zapisywany
 * lokalnie — nie mylić z [AnalyticsConsent], który jest ładunkiem wysyłanym do dostawcy.
 */
enum class AnalyticsConsentState {
    UNDECIDED,
    GRANTED,
    DENIED,
}
