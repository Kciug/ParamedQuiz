package com.rafalskrzypczyk.paramedquiz.dev

sealed interface DevOptionsUIEvents {
    object ResetOnboarding: DevOptionsUIEvents
    object ResetModularOnboarding: DevOptionsUIEvents
    object ClearTermsAcceptance: DevOptionsUIEvents
    object ResetRatingStats: DevOptionsUIEvents
    object TriggerRatingPrompt: DevOptionsUIEvents
    object ResetNews: DevOptionsUIEvents
    object ResetPurchases: DevOptionsUIEvents
}
