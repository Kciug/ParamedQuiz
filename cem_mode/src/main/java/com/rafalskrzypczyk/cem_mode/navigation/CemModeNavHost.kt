package com.rafalskrzypczyk.cem_mode.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rafalskrzypczyk.core.quiz.models.CategoryUIM
import com.rafalskrzypczyk.cem_mode.presentation.categories_screen.CemCategoriesScreen
import com.rafalskrzypczyk.cem_mode.presentation.categories_screen.CemCategoriesVM

@Composable
fun CemModeNavHost(
    onExit: () -> Unit
) {
    val cemNavController = rememberNavController()

    NavHost(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        navController = cemNavController,
        startDestination = CemCategoriesRoute(),
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween()
            ) + scaleIn()
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween()
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween()
            ) + scaleIn()
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween()
            )
        }
    ) {
        cemCategoriesDestination(
            onCategoryClick = { category ->
                if (category.subcategoriesCount > 0) {
                    cemNavController.navigate(CemCategoriesRoute(parentId = category.id, categoryTitle = category.title))
                } else {
                    // Navigate to Quiz later
                }
            },
            onNavigateBack = {
                if (!cemNavController.popBackStack()) {
                    onExit()
                }
            }
        )
    }
}

fun NavGraphBuilder.cemCategoriesDestination(
    onCategoryClick: (CategoryUIM) -> Unit,
    onNavigateBack: () -> Unit
) {
    composable<CemCategoriesRoute> {
        val viewModel = hiltViewModel<CemCategoriesVM>()
        val state = viewModel.state.collectAsStateWithLifecycle()

        CemCategoriesScreen(
            state = state.value,
            onEvent = viewModel::onEvent,
            onNavigateBack = onNavigateBack,
            onCategoryClick = onCategoryClick
        )
    }
}
