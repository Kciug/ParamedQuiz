package com.rafalskrzypczyk.ads

import android.content.SharedPreferences
import com.rafalskrzypczyk.core.analytics.AdConsent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Odczytuje stan zgód z ciągów TCF zapisanych przez formularz UMP.
 *
 * UMP zapisuje klucze `IABTCF_*` do domyślnych `SharedPreferences` (`<packageName>_preferences`) —
 * tych samych, które wstrzykuje `CoreModule`, więc nie trzeba nowego dostępu do Contextu.
 * [GoogleMobileAdsConsentManager] wystawia wyłącznie `canRequestAds`, co nie wystarcza do
 * rozróżnienia czterech typów zgody wymaganych przez Consent Mode.
 *
 * Mapowanie celów TCF (nie jest opinią prawną — do potwierdzenia przy aktualizacji polityki
 * prywatności): P1 = przechowywanie informacji na urządzeniu, P3+P4 = personalizacja reklam,
 * P7 = pomiar skuteczności reklam.
 */
@Singleton
class TcfConsentReader @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) {
    /**
     * [canRequestAds] jest używane wyłącznie jako fallback, gdy nie ma jeszcze ciągu TCF
     * (pierwsze uruchomienie, przed pokazaniem formularza).
     */
    fun read(canRequestAds: Boolean): AdConsent {
        if (gdprApplies() == GDPR_DOES_NOT_APPLY) return AdConsent.granted()

        val purposes = purposeConsents()
            ?: return if (canRequestAds) AdConsent.granted() else AdConsent.denied()

        val deviceStorage = purposes.hasConsentFor(PURPOSE_STORE_INFO)
        return AdConsent(
            adStorage = deviceStorage,
            adUserData = deviceStorage && purposes.hasConsentFor(PURPOSE_MEASURE_ADS),
            adPersonalization = purposes.hasConsentFor(PURPOSE_ADS_PROFILE) &&
                purposes.hasConsentFor(PURPOSE_SELECT_PERSONALISED_ADS),
        )
    }

    private fun gdprApplies(): Int = runCatching {
        sharedPreferences.getInt(KEY_GDPR_APPLIES, GDPR_UNKNOWN)
    }.getOrDefault(GDPR_UNKNOWN)

    private fun purposeConsents(): String? = runCatching {
        sharedPreferences.getString(KEY_PURPOSE_CONSENTS, null)
    }.getOrNull()?.takeIf { it.isNotBlank() }

    /** Ciąg to bitfield, gdzie znak o indeksie 0 odpowiada celowi nr 1. */
    private fun String.hasConsentFor(purpose: Int): Boolean = getOrNull(purpose - 1) == CONSENT_GRANTED

    private companion object {
        const val KEY_GDPR_APPLIES = "IABTCF_gdprApplies"
        const val KEY_PURPOSE_CONSENTS = "IABTCF_PurposeConsents"

        const val GDPR_UNKNOWN = -1
        const val GDPR_DOES_NOT_APPLY = 0

        const val PURPOSE_STORE_INFO = 1
        const val PURPOSE_ADS_PROFILE = 3
        const val PURPOSE_SELECT_PERSONALISED_ADS = 4
        const val PURPOSE_MEASURE_ADS = 7

        const val CONSENT_GRANTED = '1'
    }
}
