package com.rafalskrzypczyk.home_screen.presentation.home_page

import android.app.Activity

sealed interface HomeUIEvents {
    data object GetData : HomeUIEvents
    data object OpenTranslationModePurchaseSheet : HomeUIEvents
    data object CloseTranslationModePurchaseSheet : HomeUIEvents
    data object OpenSwipeModePurchaseSheet : HomeUIEvents
    data object CloseSwipeModePurchaseSheet : HomeUIEvents
    class BuyTranslationMode(val activity: Activity) : HomeUIEvents
    class BuySwipeMode(val activity: Activity) : HomeUIEvents
    data object NavigationConsumed : HomeUIEvents

    data class OnRatingSelected(val rating: Int) : HomeUIEvents
    data object OnDismissRating : HomeUIEvents
    data object OnRateStore : HomeUIEvents
    data object OnSendFeedback : HomeUIEvents
    data class OnFeedbackChanged(val feedback: String) : HomeUIEvents
    data object OnNeverAskAgain : HomeUIEvents
    data object OnBackToRating : HomeUIEvents
    data object OnFeedbackSuccessConsumed : HomeUIEvents
    data object OnFeedbackErrorConsumed : HomeUIEvents
}

sealed interface HomeSideEffect {
    data class PurchaseSuccess(val modeId: String) : HomeSideEffect
    data object LaunchReviewFlow : HomeSideEffect
    data object FeedbackSuccess : HomeSideEffect
}
