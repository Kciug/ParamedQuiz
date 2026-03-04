package com.rafalskrzypczyk.cem_mode.presentation.categories_screen

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.quiz.models.CategoryUIM

data class CemCategoriesState(
    val categories: Response<List<CategoryUIM>> = Response.Loading,
    val title: String = ""
)
