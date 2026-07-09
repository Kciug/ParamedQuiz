package com.rafalskrzypczyk.notifications

/** Wynik oceny „który (jeśli w ogóle) reminder wysłać teraz". */
sealed interface ReminderDecision {
    /** Nic nie wysyłaj (np. dzień już zaliczony). */
    data object None : ReminderDecision

    /** Bazowe przypomnienie o nauce. */
    data object Daily : ReminderDecision

    /** Powtórka słabych pytań. */
    data object Revision : ReminderDecision

    /** Seria zagrożona — [streak] to aktualna liczba dni. */
    data class Streak(val streak: Int) : ReminderDecision

    /** Reaktywacja — [day] to próg (np. 7 lub 14). */
    data class Winback(val day: Int) : ReminderDecision
}
