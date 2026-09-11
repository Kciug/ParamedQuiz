package com.rafalskrzypczyk.home_screen.presentation.privacy_consent

sealed interface PrivacyConsentUIEvents {
    data object Grant : PrivacyConsentUIEvents
    data object Deny : PrivacyConsentUIEvents
}
