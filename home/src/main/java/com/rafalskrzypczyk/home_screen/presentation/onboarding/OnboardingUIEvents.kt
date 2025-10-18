package com.rafalskrzypczyk.home_screen.presentation.onboarding

sealed interface OnboardingUIEvents {
    object CheckIsLogged: OnboardingUIEvents
}