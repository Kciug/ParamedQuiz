package com.rafalskrzypczyk.core.analytics

import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Bramka jednorazowości dla `notification_permission`.
 *
 * Wynik systemowego dialogu `POST_NOTIFICATIONS` wraca w trzech niezależnych miejscach (prompt na
 * ekranie głównym, przełącznik w ustawieniach powiadomień, opcje deweloperskie), a każde z nich
 * może zostać uruchomione wielokrotnie. Android pokazuje dialog najwyżej dwa razy i po trwałej
 * odmowie oddaje `false` natychmiast, więc bez wspólnej bramki jedno tapnięcie przełącznika
 * produkowałoby fałszywe `granted = 0`.
 *
 * Bramka jest **trwała**, nie sesyjna: pole w ViewModelu ginie razem z ekranem, a prompt na Home
 * jest uprawniony do trzech wyświetleń na instalację.
 */
@Singleton
class NotificationPermissionTracker @Inject constructor(
    private val analyticsLogger: AnalyticsLogger,
    private val sharedPreferences: SharedPreferencesApi,
) {
    fun onSystemDialogAnswered(granted: Boolean) {
        if (sharedPreferences.isNotificationPermissionAsked()) return
        sharedPreferences.setNotificationPermissionAsked()
        analyticsLogger.log(AnalyticsEvent.NotificationPermissionAnswered(granted))
    }
}
