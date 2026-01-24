package com.rafalskrzypczyk.paramedquiz

sealed interface MainActivityUIEvents {
    data object OnboardingFinished : MainActivityUIEvents
}
