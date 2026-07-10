package com.rafalskrzypczyk.paramedquiz.dev

sealed interface DevOptionsUIEvents {
    object ResetOnboarding: DevOptionsUIEvents
    object ResetModularOnboarding: DevOptionsUIEvents
    object ClearTermsAcceptance: DevOptionsUIEvents
    object ResetAdsConsent: DevOptionsUIEvents
    object ResetRatingStats: DevOptionsUIEvents
    object TriggerRatingPrompt: DevOptionsUIEvents
    object ResetNews: DevOptionsUIEvents
    object ResetPurchases: DevOptionsUIEvents
    object SendTestNotification: DevOptionsUIEvents
    object TriggerNotificationConsent: DevOptionsUIEvents
    object TriggerReminderNow: DevOptionsUIEvents
    object SimStreakPending: DevOptionsUIEvents
    object SimInactive7: DevOptionsUIEvents
    object SimInactive14: DevOptionsUIEvents
    object SimWeakQuestions: DevOptionsUIEvents
    object ForceConfigRefresh: DevOptionsUIEvents
    object SimulateNewsNotification: DevOptionsUIEvents
    object SimulateMarketingNotification: DevOptionsUIEvents
}
