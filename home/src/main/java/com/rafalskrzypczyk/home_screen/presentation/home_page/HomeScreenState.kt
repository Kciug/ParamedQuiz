package com.rafalskrzypczyk.home_screen.presentation.home_page

import com.rafalskrzypczyk.score.domain.StreakState

data class HomeScreenState (
    val userScore: Int = 0,
    val userStreak: Int = 0,
    val userStreakState: StreakState = StreakState.MISSED,
    val isUserLoggedIn: Boolean = false,
    val userAvatar: String? = null,
    val isNewDailyExerciseAvailable: Boolean = false,
    val userName: String? = null,
    val isTranslationModeUnlocked: Boolean = false,
    val translationModePrice: String? = null,
    val showTranslationModePurchaseDialog: Boolean = false
)