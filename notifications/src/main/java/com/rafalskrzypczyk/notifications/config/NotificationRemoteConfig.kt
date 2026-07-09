package com.rafalskrzypczyk.notifications.config

import kotlinx.serialization.Serializable

/**
 * Konfiguracja powiadomień (parametry + pule tekstów) pobierana z Firestore i cache'owana lokalnie.
 * [DEFAULT] to jedyne źródło fallbacku (Załącznik A spec + domyślne parametry decidera).
 */
@Serializable
data class NotificationRemoteConfig(
    val streakMinValue: Int,
    val winbackDays: List<Int>,
    val revisionIntervalDays: Int,
    val dailyTexts: List<String>,
    val streakTexts: List<String>,
    val revisionTexts: List<String>,
    val winbackTexts: Map<Int, String>
) {
    companion object {
        val DEFAULT = NotificationRemoteConfig(
            streakMinValue = 2,
            winbackDays = listOf(7, 14),
            revisionIntervalDays = 3,
            dailyTexts = listOf(
                "⏱️ 5 minut nauki dziś? Rozwiąż szybki zestaw pytań.",
                "💊 Czas na dawkę wiedzy — sprawdź się z pytań.",
                "🚑 Gotowy na dziś? Kilka pytań czeka.",
                "📚 Twoja codzienna porcja pytań jest gotowa.",
                "🧠 Utrwal wiedzę — krótka sesja przed snem?"
            ),
            streakTexts = listOf(
                "🔥 Masz serię {streak} dni! Nie przerywaj jej — jedna sesja wystarczy.",
                "🔥 {streak}-dniowa passa jest zagrożona. Wskocz na chwilę, żeby ją utrzymać."
            ),
            revisionTexts = listOf(
                "🔁 Masz pytania, które idą Ci słabo — powtórz je i podbij wynik.",
                "🔁 Czas na powtórkę słabych punktów. Kilka pytań wystarczy, żeby je opanować.",
                "🎯 Twoje najsłabsze pytania czekają na rewanż."
            ),
            winbackTexts = mapOf(
                7 to "👋 Dawno Cię nie było — wróć na krótką sesję i nie trać formy.",
                14 to "📚 Twoja wiedza czeka. 5 pytań, żeby wrócić do rytmu?"
            )
        )
    }
}
