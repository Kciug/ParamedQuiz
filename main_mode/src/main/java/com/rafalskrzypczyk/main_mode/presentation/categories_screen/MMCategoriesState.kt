package com.rafalskrzypczyk.main_mode.presentation.categories_screen

import androidx.compose.runtime.Immutable
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.score.domain.StreakState

@Immutable
data class MMCategoriesState (
    val userScore: Int = 0,
    val userStreak: Int = 0,
    val userStreakState: StreakState = StreakState.MISSED,
    val isUserLoggedIn: Boolean = false,
    val isPremium: Boolean = false,
    val userAvatar: String? = null,
    val responseState: ResponseState = ResponseState.Idle,
    val userIcon: Int? = null,
    val categories: List<CategoryUIM> = emptyList(),
    val unlockCategory: Boolean = false,
    val selectedCategoryForPurchase: CategoryUIM? = null,
    val productPrice: String? = null
)