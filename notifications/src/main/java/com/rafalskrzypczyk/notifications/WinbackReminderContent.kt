package com.rafalskrzypczyk.notifications

/**
 * Lokalne teksty Win-backu (fallback — Załącznik A spec), per próg dni nieaktywności.
 */
object WinbackReminderContent {
    fun forDay(day: Int): String = when (day) {
        7 -> "👋 Dawno Cię nie było — wróć na krótką sesję i nie trać formy."
        14 -> "📚 Twoja wiedza czeka. 5 pytań, żeby wrócić do rytmu?"
        else -> "👋 Wróć do nauki — Twoje pytania czekają."
    }
}
