package com.rafalskrzypczyk.firestore.config

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.rafalskrzypczyk.core.domain.config.GameplayConfig
import com.rafalskrzypczyk.core.domain.config.GameplayConfigProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gameplay config oparty o Firebase Remote Config. RC SDK sam trzyma cache, bramkę TTL
 * ([REFRESH_INTERVAL_SECONDS]) i persystencję między startami. Defaulty in-app =
 * [GameplayConfig.DEFAULT]. Akcesory clampują wartości — parametr publikowany w konsoli
 * trafia do userów bez bramki review, więc sanity-limit chroni przed błędną wartością.
 */
@Singleton
class RemoteConfigGameplayProvider @Inject constructor() : GameplayConfigProvider {

    private val remoteConfig = Firebase.remoteConfig.apply {
        setConfigSettingsAsync(
            remoteConfigSettings { minimumFetchIntervalInSeconds = REFRESH_INTERVAL_SECONDS }
        )
        setDefaultsAsync(
            mapOf(
                KEY_AD_FREQUENCY to GameplayConfig.DEFAULT.adFrequency.toLong(),
                KEY_EXIT_AD_THRESHOLD to GameplayConfig.DEFAULT.exitAdThreshold.toLong(),
                KEY_CORRECT_POINTS to GameplayConfig.DEFAULT.correctPoints.toLong(),
                KEY_FIRST_CORRECT_POINTS to GameplayConfig.DEFAULT.firstCorrectPoints.toLong(),
                KEY_STREAK_POINTS_THRESHOLD to GameplayConfig.DEFAULT.streakPointsThreshold.toLong(),
                KEY_DAILY_QUESTIONS to GameplayConfig.DEFAULT.dailyExerciseQuestionsAmount.toLong()
            )
        )
    }

    override fun adFrequency(): Int =
        remoteConfig.getLong(KEY_AD_FREQUENCY).toInt().coerceIn(1, 500)

    override fun exitAdThreshold(): Int =
        remoteConfig.getLong(KEY_EXIT_AD_THRESHOLD).toInt().coerceIn(1, 500)

    override fun correctPoints(): Int =
        remoteConfig.getLong(KEY_CORRECT_POINTS).toInt().coerceIn(0, 100_000)

    override fun firstCorrectPoints(): Int =
        remoteConfig.getLong(KEY_FIRST_CORRECT_POINTS).toInt().coerceIn(0, 100_000)

    override fun streakPointsThreshold(): Int =
        remoteConfig.getLong(KEY_STREAK_POINTS_THRESHOLD).toInt().coerceIn(1, 1_000)

    override fun dailyExerciseQuestionsAmount(): Int =
        remoteConfig.getLong(KEY_DAILY_QUESTIONS).toInt().coerceIn(1, 100)

    override suspend fun refresh(force: Boolean) {
        if (force) {
            remoteConfig.fetch(0).await()
            remoteConfig.activate().await()
        } else {
            remoteConfig.fetchAndActivate().await()
        }
    }

    companion object {
        private const val REFRESH_INTERVAL_SECONDS = 24L * 60 * 60

        private const val KEY_AD_FREQUENCY = "adFrequency"
        private const val KEY_EXIT_AD_THRESHOLD = "exitAdThreshold"
        private const val KEY_CORRECT_POINTS = "correctPoints"
        private const val KEY_FIRST_CORRECT_POINTS = "firstCorrectPoints"
        private const val KEY_STREAK_POINTS_THRESHOLD = "streakPointsThreshold"
        private const val KEY_DAILY_QUESTIONS = "dailyExerciseQuestionsAmount"
    }
}
