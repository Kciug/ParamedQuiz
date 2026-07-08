package com.rafalskrzypczyk.notifications

/**
 * Lokalna pula tekstów Daily Remindera (fallback — Załącznik A spec).
 * W kolejnym etapie zostanie zastąpiona/uzupełniona treściami z Firestore.
 */
object DailyReminderContent {
    private val texts = listOf(
        "⏱️ 5 minut nauki dziś? Rozwiąż szybki zestaw pytań.",
        "💊 Czas na dawkę wiedzy — sprawdź się z pytań.",
        "🚑 Gotowy na dziś? Kilka pytań czeka.",
        "📚 Twoja codzienna porcja pytań jest gotowa.",
        "🧠 Utrwal wiedzę — krótka sesja przed snem?"
    )

    fun random(): String = texts.random()
}
