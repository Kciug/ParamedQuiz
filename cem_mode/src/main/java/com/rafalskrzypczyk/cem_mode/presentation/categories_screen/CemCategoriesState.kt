package com.rafalskrzypczyk.cem_mode.presentation.categories_screen

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.quiz.models.CategoryUIM
import com.rafalskrzypczyk.score.domain.StreakState

data class CemCategoriesState(
    val userScore: Int = 0,
    val userStreak: Int = 0,
    val userStreakState: StreakState = StreakState.MISSED,
    val isUserLoggedIn: Boolean = false,
    val isPremium: Boolean = false,
    val userAvatar: String? = null,
    val categories: Response<List<CategoryUIM>> = Response.Loading,
    val title: String = "",
    val parentCategory: CategoryUIM? = null
)
