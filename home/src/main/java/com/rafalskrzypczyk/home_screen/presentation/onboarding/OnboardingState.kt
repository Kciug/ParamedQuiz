package com.rafalskrzypczyk.home_screen.presentation.onboarding

import androidx.compose.runtime.Immutable

@Immutable
data class OnboardingState(
    val isLogged: Boolean = false,
    val userName: String = "",
    val userEmail: String = ""
)
