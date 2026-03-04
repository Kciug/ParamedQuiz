package com.rafalskrzypczyk.cem_mode.presentation.categories_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.quiz.CategoryCard
import com.rafalskrzypczyk.core.composables.top_bars.MainTopBarWithNav
import com.rafalskrzypczyk.core.quiz.models.CategoryUIM
import com.rafalskrzypczyk.score.domain.StreakState

@Composable
fun CemCategoriesScreen(
    state: CemCategoriesState,
    onEvent: (CemCategoriesUIEvents) -> Unit,
    onNavigateBack: () -> Unit,
    onUserPanel: () -> Unit,
    onCategoryClick: (CategoryUIM) -> Unit
) {
    Scaffold(
        topBar = {
            MainTopBarWithNav(
                userScore = state.userScore,
                userStreak = state.userStreak,
                isUserLoggedIn = state.isUserLoggedIn,
                isPremium = state.isPremium,
                userAvatar = state.userAvatar,
                onNavigateBack = onNavigateBack,
                onNavigateToUserPanel = onUserPanel,
                userStreakPending = state.userStreakState == StreakState.PENDING
            )
        }
    ) { paddingValues ->
        when (val response = state.categories) {
            is Response.Loading -> Loading(modifier = Modifier.fillMaxSize())
            is Response.Error -> ErrorDialog(
                errorMessage = response.error,
                onInteraction = { onEvent(CemCategoriesUIEvents.OnRetry) }
            )
            is Response.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(Dimens.DEFAULT_PADDING),
                    verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
                ) {
                    items(response.data) { category ->
                        CategoryCard(
                            category = category,
                            onClick = { onCategoryClick(category) }
                        )
                    }
                }
            }
        }
    }
}
