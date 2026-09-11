package com.rafalskrzypczyk.home_screen.presentation.onboarding

sealed interface OnboardingUIEvents {
    object CheckIsLogged: OnboardingUIEvents

    /** Zakonczenie sekwencji: przyciskiem konca albo pominieciem. */
    data class Finished(val skipped: Boolean, val lastPage: Int): OnboardingUIEvents
}