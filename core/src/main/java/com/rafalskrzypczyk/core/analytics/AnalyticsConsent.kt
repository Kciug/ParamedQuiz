package com.rafalskrzypczyk.core.analytics

/**
 * Ładunek zgód wysyłany do dostawcy (Consent Mode). Scalany przez [AnalyticsConsentManager]
 * z dwóch rozłącznych źródeł: decyzji użytkownika o analityce i stanu formularza UMP.
 */
data class AnalyticsConsent(
    val analyticsStorage: Boolean,
    val adStorage: Boolean,
    val adUserData: Boolean,
    val adPersonalization: Boolean,
)

/**
 * Zgody reklamowe — jedyne, za które odpowiada UMP. Mapowane z ciągów TCF w module `:ads`;
 * `core` nie zna ani UMP, ani Firebase.
 */
data class AdConsent(
    val adStorage: Boolean,
    val adUserData: Boolean,
    val adPersonalization: Boolean,
) {
    companion object {
        /** Stan wyjściowy, zanim formularz UMP cokolwiek rozstrzygnie. */
        fun denied() = AdConsent(
            adStorage = false,
            adUserData = false,
            adPersonalization = false,
        )

        fun granted() = AdConsent(
            adStorage = true,
            adUserData = true,
            adPersonalization = true,
        )
    }
}
