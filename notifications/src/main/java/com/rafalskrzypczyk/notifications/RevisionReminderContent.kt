package com.rafalskrzypczyk.notifications

/**
 * Lokalna pula tekstów Revision Remindera (fallback — Załącznik A spec).
 * W kolejnym etapie zostanie zastąpiona/uzupełniona treściami z Firestore.
 */
object RevisionReminderContent {
    private val texts = listOf(
        "🔁 Masz pytania, które idą Ci słabo — powtórz je i podbij wynik.",
        "🔁 Czas na powtórkę słabych punktów. Kilka pytań wystarczy, żeby je opanować.",
        "🎯 Twoje najsłabsze pytania czekają na rewanż."
    )

    fun random(): String = texts.random()
}
