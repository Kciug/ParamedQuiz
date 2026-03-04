package com.rafalskrzypczyk.cem_mode.presentation.categories_screen

import com.rafalskrzypczyk.core.quiz.models.CategoryUIM

sealed class CemCategoriesUIEvents {
    object OnRetry : CemCategoriesUIEvents()
}
