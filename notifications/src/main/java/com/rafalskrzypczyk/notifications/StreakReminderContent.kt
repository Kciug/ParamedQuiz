package com.rafalskrzypczyk.notifications

/**
 * Lokalna pula tekstów Streak Remindera (fallback — Załącznik A spec).
 * `{streak}` podstawiane liczbą dni serii.
 */
object StreakReminderContent {
    private val texts = listOf(
        "🔥 Masz serię {streak} dni! Nie przerywaj jej — jedna sesja wystarczy.",
        "🔥 {streak}-dniowa passa jest zagrożona. Wskocz na chwilę, żeby ją utrzymać."
    )

    fun random(streak: Int): String =
        texts.random().replace("{streak}", streak.toString())
}
