package com.rafalskrzypczyk.home_screen.presentation.home_screen.home_page

data class HomeScreenState(
    val isUserLoggedIn: Boolean = false,
    val userScore: Int = 0,
    val userStreak: Int = 0,
    val userAvatar: String? = null,
    val isNewDailyExerciseAvailable: Boolean = false,
)