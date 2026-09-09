package com.rafalskrzypczyk.core.analytics

/**
 * Stan zgód w rozumieniu Consent Mode. Źródłem jest UMP (formularz reklamowy) i ciągi TCF,
 * mapowane w module `:ads` — `core` nie zna ani UMP, ani Firebase.
 */
data class AnalyticsConsent(
    val analyticsStorage: Boolean,
    val adStorage: Boolean,
    val adUserData: Boolean,
    val adPersonalization: Boolean,
) {
    companion object {
        /** Stan wyjściowy przed ustaleniem zgody — konserwatywny. */
        fun denied() = AnalyticsConsent(
            analyticsStorage = false,
            adStorage = false,
            adUserData = false,
            adPersonalization = false,
        )

        fun granted() = AnalyticsConsent(
            analyticsStorage = true,
            adStorage = true,
            adUserData = true,
            adPersonalization = true,
        )
    }
}
