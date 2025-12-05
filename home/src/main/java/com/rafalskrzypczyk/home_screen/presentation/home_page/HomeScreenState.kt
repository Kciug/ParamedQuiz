package com.rafalskrzypczyk.home_screen.presentation.home_page

import androidx.compose.runtime.Immutable
import com.rafalskrzypczyk.score.domain.StreakState

@Immutable
data class HomeScreenState(
    val isUserLoggedIn: Boolean = false,
    val userName: String? = null,
    val userScore: Int = 0,
    val userStreak: Int = 0,
    val userStreakState: StreakState = StreakState.MISSED,
    val userAvatar: String? = null,
    val isNewDailyExerciseAvailable: Boolean = false,
)