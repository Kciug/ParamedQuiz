package com.rafalskrzypczyk.main_mode.presentation.categories_screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.top_bars.MainTopBarWithNav
import com.rafalskrzypczyk.score.domain.StreakState

@Composable
fun MMCategoriesScreen(
    state: MMCategoriesState,
    onEvent: (MMCategoriesUIEvents) -> Unit,
    onNavigateBack: () -> Unit,
    onUserPanel: () -> Unit,
    onStartCategory: (Long, String) -> Unit
) {
    LaunchedEffect(Unit) {
        onEvent(MMCategoriesUIEvents.GetData)
    }

    Scaffold(
        topBar = {
            MainTopBarWithNav(
                userScore = state.userScore,
                userStreak = state.userStreak,
                isUserLoggedIn = state.isUserLoggedIn,
                userAvatar = state.userAvatar,
                onNavigateBack = onNavigateBack,
                onNavigateToUserPanel = onUserPanel,
                userStreakPending = state.userStreakState == StreakState.PENDING
            )
        }
    ) { innerPadding ->
        AnimatedContent(
            modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
            targetState = state.responseState,
            transitionSpec = {
                scaleIn() togetherWith scaleOut()
            },
            label = "responseTransition"
        ) { responseState ->
            when (responseState) {
                ResponseState.Idle -> {}
                ResponseState.Loading -> Loading()
                is ResponseState.Error -> ErrorDialog(responseState.message) {
                    onNavigateBack()
                }
                ResponseState.Success -> MMCategoriesScreenContent(
                    contentPadding = innerPadding,
                    categories = state.categories,
                    onStartCategory = onStartCategory,
                    onUnlockCategory = { onEvent(MMCategoriesUIEvents.OnUnlockCategory(it)) }
                )
            }
        }
    }
}

@Composable
fun MMCategoriesScreenContent(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    categories: List<CategoryUIM>,
    onStartCategory: (Long, String) -> Unit,
    onUnlockCategory: (Long) -> Unit
) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val contentPaddingAdjusted = PaddingValues(
        top = contentPadding.calculateTopPadding() - statusBarPadding + Dimens.DEFAULT_PADDING,
        start = Dimens.DEFAULT_PADDING,
        end = Dimens.DEFAULT_PADDING,
        bottom = contentPadding.calculateBottomPadding() + Dimens.DEFAULT_PADDING
    )


    LazyColumn(
        modifier = modifier.statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING),
        contentPadding = contentPaddingAdjusted
    ) {
        categories.forEach { category ->
            item {
                CategoryCard(
                    category = category,
                    onClick = if(category.unlocked) {
                        { onStartCategory(category.id, category.title) }
                    } else {
                        { onUnlockCategory(category.id) }
                    }
                )
            }
        }
    }
}

