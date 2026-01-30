package com.rafalskrzypczyk.home_screen.presentation.home_page

import android.app.Activity

sealed interface HomeUIEvents {
    object GetData : HomeUIEvents
    object OpenTranslationModePurchaseDialog : HomeUIEvents
    object CloseTranslationModePurchaseDialog : HomeUIEvents
    class BuyTranslationMode(val activity: Activity) : HomeUIEvents
}