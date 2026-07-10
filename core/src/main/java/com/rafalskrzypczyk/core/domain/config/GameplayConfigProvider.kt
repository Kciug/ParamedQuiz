package com.rafalskrzypczyk.core.domain.config

/**
 * Dostęp do strojalnych parametrów rozgrywki (reklamy, zadanie dnia, punktacja, seria).
 * Implementacja żyje w module firebase-aware (Remote Config); interfejs tu, bo `core`
 * nie może zależeć od modułów firebase, a konsumenci (m.in. QuizAdHandler) są w `core`.
 */
interface GameplayConfigProvider {
    fun adFrequency(): Int
    fun exitAdThreshold(): Int
    fun correctPoints(): Int
    fun firstCorrectPoints(): Int
    fun streakPointsThreshold(): Int
    fun dailyExerciseQuestionsAmount(): Int

    /** Pobiera świeżą konfigurację. [force] pomija bramkę TTL. Ciche na błędzie. */
    suspend fun refresh(force: Boolean = false)
}
