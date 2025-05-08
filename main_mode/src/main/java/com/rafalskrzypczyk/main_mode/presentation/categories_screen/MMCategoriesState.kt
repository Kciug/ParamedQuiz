package com.rafalskrzypczyk.main_mode.presentation.categories_screen

import com.rafalskrzypczyk.core.api_response.ResponseState


data class MMCategoriesState (
    val responseState: ResponseState = ResponseState.Idle,
    val userIcon: Int? = null,
    val categories: List<CategoryUIM> = emptyList(),
    val unlockCategory: Boolean = false
)