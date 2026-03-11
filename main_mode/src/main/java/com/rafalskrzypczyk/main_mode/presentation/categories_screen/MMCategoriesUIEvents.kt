package com.rafalskrzypczyk.main_mode.presentation.categories_screen

import android.app.Activity
import com.rafalskrzypczyk.core.quiz.models.CategoryUIM

sealed interface MMCategoriesUIEvents {
    object GetData : MMCategoriesUIEvents
    class OpenPurchaseDialog(val category: CategoryUIM) : MMCategoriesUIEvents
    object ClosePurchaseDialog : MMCategoriesUIEvents
    class BuyCategory(val activity: Activity) : MMCategoriesUIEvents
}
