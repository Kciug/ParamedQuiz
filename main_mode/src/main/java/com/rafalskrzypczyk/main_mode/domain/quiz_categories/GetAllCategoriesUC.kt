package com.rafalskrzypczyk.main_mode.domain.quiz_categories

import com.rafalskrzypczyk.main_mode.domain.MainModeRepository
import javax.inject.Inject

class GetAllCategoriesUC @Inject constructor(
    private val repository: MainModeRepository
) {
    operator fun invoke() = repository.getAllCategories()
}