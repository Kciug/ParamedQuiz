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
}