package com.rafalskrzypczyk.home_screen.presentation.privacy_consent

import androidx.lifecycle.ViewModel
import com.rafalskrzypczyk.core.analytics.AnalyticsConsentManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Ekran nie ma stanu — jedyne, co robi, to zapisanie decyzji. Świadomie nie wysyła też żadnego
 * zdarzenia analitycznego: przed zgodą nic nie jest logowane, więc takie zdarzenie nigdy by nie
 * wyszło, a po zgodzie mierzyłoby coś, czego nie da się porównać z odmowami.
 */
@HiltViewModel
class PrivacyConsentVM @Inject constructor(
    private val consentManager: AnalyticsConsentManager,
) : ViewModel() {

    fun onEvent(event: PrivacyConsentUIEvents) {
        when (event) {
            PrivacyConsentUIEvents.Grant -> consentManager.grant()
            PrivacyConsentUIEvents.Deny -> consentManager.deny()
        }
    }
}
