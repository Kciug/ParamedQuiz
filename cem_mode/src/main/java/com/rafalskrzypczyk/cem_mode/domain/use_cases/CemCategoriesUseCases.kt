package com.rafalskrzypczyk.cem_mode.domain.use_cases

data class CemCategoriesUseCases(
    val getCemCategories: GetCemCategoriesUseCase,
    val getUpdatedCemCategories: GetUpdatedCemCategoriesUseCase,
    val getCemCategory: GetCemCategoryUseCase
)
