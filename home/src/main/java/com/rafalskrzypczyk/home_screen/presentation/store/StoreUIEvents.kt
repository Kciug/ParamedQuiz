package com.rafalskrzypczyk.home_screen.presentation.store

import android.app.Activity

sealed interface StoreUIEvents {
    object GetData : StoreUIEvents
    data class BuyProduct(val activity: Activity, val productId: String) : StoreUIEvents
    object ConsumeError : StoreUIEvents
}

sealed interface StoreSideEffect {
    data class PurchaseSuccess(val productId: String) : StoreSideEffect
}
