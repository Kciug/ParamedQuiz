package com.rafalskrzypczyk.main_mode.presentation.categories_screen

sealed interface MMCategoriesUIEvents {
    object GetData : MMCategoriesUIEvents
    class OnUnlockCategory(val categoryId: Long) : MMCategoriesUIEvents
    object DiscardUnlockCategory : MMCategoriesUIEvents
}