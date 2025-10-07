package com.rafalskrzypczyk.main_mode.presentation.categories_screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.MainTopBarWithNav
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
        val modifier = Modifier.padding(innerPadding)

        AnimatedContent(
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
                    modifier = modifier,
                    categories = state.categories,
                    onStartCategory = onStartCategory,
                    onUnlockCategory = { onEvent(MMCategoriesUIEvents.OnUnlockCategory(it)) }
                )
            }
        }

//        when(state.responseState) {
//            ResponseState.Idle -> {}
//            ResponseState.Loading -> Loading()
//            is ResponseState.Error -> ErrorDialog(state.responseState.message) { onNavigateBack() }
//            ResponseState.Success -> MMCategoriesScreenContent(
//                modifier = modifier,
//                categories = state.categories,
//                onStartCategory = onStartCategory,
//                onUnlockCategory = { onEvent(MMCategoriesUIEvents.OnUnlockCategory(it)) }
//            )
//        }
    }
}

@Composable
fun MMCategoriesScreenContent(
    modifier: Modifier = Modifier,
    categories: List<CategoryUIM>,
    onStartCategory: (Long, String) -> Unit,
    onUnlockCategory: (Long) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING),
        contentPadding = PaddingValues(Dimens.DEFAULT_PADDING)
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

