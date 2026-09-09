package com.rafalskrzypczyk.core.analytics

/**
 * Właściwości użytkownika. Wartości są zawsze tekstowe — GA4 nie ma typu liczbowego dla właściwości.
 */
enum class AnalyticsUserProperty(val propertyName: String) {
    /** `release` | `staging` | `debug`. Odfiltrowuje ruch testowy z internal tracka. */
    BUILD_TYPE("build_type"),

    /** `none` | `partial` | `full` */
    PREMIUM_TIER("premium_tier"),

    /** Premium/„brak reklam" albo globalny wyłącznik z Remote Config. */
    ADS_DISABLED("ads_disabled"),

    IS_LOGGED_IN("is_logged_in"),

    /** Przełącznik w aplikacji, nie systemowe uprawnienie POST_NOTIFICATIONS. */
    NOTIFICATIONS_ON("notifications_on"),

    /** `0` | `1_3` | `4_7` | `8_30` | `30_plus` */
    STREAK_BUCKET("streak_bucket"),
}
