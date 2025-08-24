package com.rafalskrzypczyk.main_mode.presentation.categories_screen

import com.rafalskrzypczyk.core.api_response.ResponseState

data class MMCategoriesState (
    val userScore: Int = 0,
    val userStreak: Int = 0,
    val isUserLoggedIn: Boolean = false,
    val userAvatar: String? = null,
    val responseState: ResponseState = ResponseState.Idle,
    val userIcon: Int? = null,
    val categories: List<CategoryUIM> = emptyList(),
    val unlockCategory: Boolean = false
)