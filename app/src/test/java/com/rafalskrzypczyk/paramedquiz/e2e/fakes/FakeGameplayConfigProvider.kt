package com.rafalskrzypczyk.paramedquiz.e2e.fakes

import com.rafalskrzypczyk.core.domain.config.GameplayConfig
import com.rafalskrzypczyk.core.domain.config.GameplayConfigProvider

/**
 * Fake configu rozgrywki bez Remote Config (Firebase). Zwraca wartości domyślne [GameplayConfig.DEFAULT];
 * testy mogą je nadpisać (np. mniejszy [dailyExerciseQuestionsAmount] dla krótszych sesji).
 */
class FakeGameplayConfigProvider(
    var config: GameplayConfig = GameplayConfig.DEFAULT
) : GameplayConfigProvider {
    override fun adFrequency(): Int = config.adFrequency
    override fun exitAdThreshold(): Int = config.exitAdThreshold
    override fun correctPoints(): Int = config.correctPoints
    override fun firstCorrectPoints(): Int = config.firstCorrectPoints
    override fun streakPointsThreshold(): Int = config.streakPointsThreshold
    override fun dailyExerciseQuestionsAmount(): Int = config.dailyExerciseQuestionsAmount
    override suspend fun refresh(force: Boolean) = Unit
}
