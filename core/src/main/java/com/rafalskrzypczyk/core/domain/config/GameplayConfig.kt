package com.rafalskrzypczyk.core.domain.config

/**
 * Domyślne wartości gameplay configu — fallback in-app dla Remote Config
 * (brak parametru w konsoli / offline / błąd fetch).
 */
data class GameplayConfig(
    val adsEnabled: Boolean,
    val adFrequency: Int,
    val exitAdThreshold: Int,
    val correctPoints: Int,
    val firstCorrectPoints: Int,
    val streakPointsThreshold: Int,
    val dailyExerciseQuestionsAmount: Int
) {
    companion object {
        val DEFAULT = GameplayConfig(
            adsEnabled = true,
            adFrequency = 20,
            exitAdThreshold = 10,
            correctPoints = 100,
            firstCorrectPoints = 300,
            streakPointsThreshold = 3,
            dailyExerciseQuestionsAmount = 3
        )
    }
}
