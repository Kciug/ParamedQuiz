package com.rafalskrzypczyk.paramedquiz.dev

sealed interface DevOptionsUIEvents {
    object ResetOnboarding: DevOptionsUIEvents
    object ResetModularOnboarding: DevOptionsUIEvents
    object ClearTermsAcceptance: DevOptionsUIEvents
}