package com.rafalskrzypczyk.main_mode.presentation.categories_screen

import android.app.Activity

sealed interface MMCategoriesUIEvents {
    object GetData : MMCategoriesUIEvents
    class OnUnlockCategory(val categoryId: Long) : MMCategoriesUIEvents
    object DiscardUnlockCategory : MMCategoriesUIEvents
    class OpenPurchaseDialog(val category: CategoryUIM) : MMCategoriesUIEvents
    object ClosePurchaseDialog : MMCategoriesUIEvents
    class BuyCategory(val activity: Activity) : MMCategoriesUIEvents
}