package com.rafalskrzypczyk.paramedquiz.dev

import androidx.compose.runtime.Immutable

@Immutable
data class DevOptionsState(
    // Podgląd globalnego wyłącznika reklam z Remote Config — bez tego jedyną weryfikacją
    // flagi jest rozegranie pełnego quizu i sprawdzenie, czy reklama się pojawi.
    val areAdsEnabled: Boolean = true
)
