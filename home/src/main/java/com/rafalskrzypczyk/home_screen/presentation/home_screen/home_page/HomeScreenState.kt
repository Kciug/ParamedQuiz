package com.rafalskrzypczyk.home_screen.presentation.home_screen.home_page

import com.rafalskrzypczyk.score.domain.StreakState

data class HomeScreenState(
    val isUserLoggedIn: Boolean = false,
    val userScore: Int = 0,
    val userStreak: Int = 0,
    val userStreakState: StreakState = StreakState.MISSED,
    val userAvatar: String? = null,
    val isNewDailyExerciseAvailable: Boolean = false,
)