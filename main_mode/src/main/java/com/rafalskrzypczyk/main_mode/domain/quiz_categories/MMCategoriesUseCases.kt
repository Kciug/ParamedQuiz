package com.rafalskrzypczyk.main_mode.domain.quiz_categories

import javax.inject.Inject


data class MMCategoriesUseCases @Inject constructor(
    val getAllCategories: GetAllCategoriesUC,
)
