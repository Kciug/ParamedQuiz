package com.rafalskrzypczyk.home_screen.presentation.onboarding

data class OnboardingState(
    val isLogged: Boolean = false,
    val userName: String = "",
    val userEmail: String = ""
)
