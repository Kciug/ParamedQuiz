package com.rafalskrzypczyk.main_mode.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rafalskrzypczyk.main_mode.presentation.categories_screen.MMCategoriesScreen
import com.rafalskrzypczyk.main_mode.presentation.categories_screen.MMCategoriesVM
import kotlinx.serialization.Serializable

@Composable
fun MainModeNavHost(
    onExit: () -> Unit,
    onUserPanel: () -> Unit
) {
    val mainModeNavController = rememberNavController()

    NavHost(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        navController = mainModeNavController,
        startDestination = Categories,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                animationSpec = tween()
            ) + scaleIn()
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                animationSpec = tween()
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                animationSpec = tween()
            ) + scaleIn()
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                animationSpec = tween()
            )
        }
    ) {
        mainModeCategoriesDestination(
            onExit = onExit,
            onUserPanel = onUserPanel,
            onStartCategory = {}
        )

    }
}

@Serializable
object Categories

fun NavGraphBuilder.mainModeCategoriesDestination(
    onExit: () -> Unit,
    onUserPanel: () -> Unit,
    onStartCategory: (Long) -> Unit
) {
    composable<Categories> {
        val viewModel = hiltViewModel<MMCategoriesVM>()
        val state = viewModel.state.collectAsStateWithLifecycle()

        MMCategoriesScreen(
            state = state.value,
            onEvent = viewModel::onEvent,
            onNavigateBack = onExit,
            onUserPanel = onUserPanel,
            onStartCategory = onStartCategory
        )
    }
}