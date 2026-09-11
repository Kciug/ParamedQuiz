package com.rafalskrzypczyk.core.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.Flow

/**
 * Logger dla composables, ktore nie maja wlasnego ViewModelu — dzis wylacznie zagniezdzone
 * `NavHost`y trybow raportujace `screen_view`. Dostarczany raz, w `MainActivity`.
 *
 * Brak domyslnej wartosci jest celowy: cicha atrapa ukrylaby brak dostawcy, a jedyne miejsce,
 * ktore komponuje te `NavHost`y, to glowna nawigacja.
 */
val LocalAnalyticsLogger = staticCompositionLocalOf<AnalyticsLogger> {
    error("LocalAnalyticsLogger nie jest dostarczony — brakuje CompositionLocalProvider w MainActivity")
}

/**
 * Raportuje `screen_view` dla zagniezdzonego `NavHost`a.
 *
 * [key] to kontroler nawigacji (stabilny dzieki `rememberNavController`), a [screens] mapuje jego
 * wpisy na zdarzenia — `null` pomija wpis (np. onboarding trybu, ktorego iOS nie raportuje jako
 * ekranu). Moduly trybow nie moga wspoldzielic typow nawigacji przez `:core`, stad `Flow`
 * zamiast kontrolera w sygnaturze.
 */
@Composable
fun TrackScreenViews(key: Any, screens: () -> Flow<AnalyticsEvent.ScreenView?>) {
    val analyticsLogger = LocalAnalyticsLogger.current
    LaunchedEffect(key) {
        screens().collect { screen -> screen?.let(analyticsLogger::log) }
    }
}
