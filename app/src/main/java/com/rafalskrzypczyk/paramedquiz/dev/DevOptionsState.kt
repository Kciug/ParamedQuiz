package com.rafalskrzypczyk.paramedquiz.dev

import androidx.compose.runtime.Immutable
import com.rafalskrzypczyk.core.analytics.AdUnit
import com.rafalskrzypczyk.core.analytics.AnalyticsConsentState
import com.rafalskrzypczyk.core.analytics.RecentAnalyticsEvents

@Immutable
data class DevOptionsState(
    // Podgląd globalnego wyłącznika reklam z Remote Config — bez tego jedyną weryfikacją
    // flagi jest rozegranie pełnego quizu i sprawdzenie, czy reklama się pojawi.
    val areAdsEnabled: Boolean = true,

    // Analityka: stan, ktorego nie widac nigdzie poza przelacznikiem w ustawieniach konta,
    // i bufor tego, co faktycznie przeszlo przez bramke zgody.
    val analyticsConsent: AnalyticsConsentState = AnalyticsConsentState.UNDECIDED,
    val buildType: String = "",
    val adUnit: AdUnit = AdUnit.TEST,
    val recentAnalytics: List<RecentAnalyticsEvents.Entry> = emptyList(),
)
