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
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rafalskrzypczyk.cem_mode.presentation.categories_screen.CemCategoriesScreen
import com.rafalskrzypczyk.cem_mode.presentation.categories_screen.CemCategoriesVM
import com.rafalskrzypczyk.cem_mode.presentation.quiz_screen.CemQuizVM
import com.rafalskrzypczyk.cem_mode.presentation.onboarding.CemOnboardingScreen
import com.rafalskrzypczyk.cem_mode.presentation.onboarding.CemOnboardingVM
import com.rafalskrzypczyk.cem_mode.presentation.CemModeEntryVM
import com.rafalskrzypczyk.core.quiz.models.CategoryUIM
import com.rafalskrzypczyk.main_mode.presentation.quiz_base.MMQuizScreen

@Composable
fun CemModeNavHost(
    onExit: () -> Unit,
    onUserPanel: () -> Unit
) {
    val cemNavController = rememberNavController()
    val entryViewModel = hiltViewModel<CemModeEntryVM>()
    val startDestination = if (entryViewModel.isOnboardingSeen()) CemCategoriesRoute() else CemOnboardingRoute

    NavHost(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        navController = cemNavController,
        startDestination = startDestination,
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
        cemOnboardingDestination(
            onNavigateBack = {
                if (!cemNavController.popBackStack()) {
                    onExit()
                }
            },
            onFinishOnboarding = {
                cemNavController.navigate(CemCategoriesRoute()) {
                    popUpTo(CemOnboardingRoute) { inclusive = true }
                }
            }
        )
        cemCategoriesDestination(
            onUserPanel = onUserPanel,
            onCategoryClick = { category ->
                if (category.subcategoriesCount > 0) {
                    cemNavController.navigate(CemCategoriesRoute(parentId = category.id, categoryTitle = category.title))
                } else {
                    cemNavController.navigate(CemQuizRoute(categoryId = category.id, categoryTitle = category.title))
                }
            },
            onQuizClick = { category ->
                cemNavController.navigate(CemQuizRoute(categoryId = category.id, categoryTitle = category.title))
            },
            onNavigateBack = {
                if (!cemNavController.popBackStack()) {
                    onExit()
                }
            }
        )
        cemQuizDestination(
            onNavigateBack = {
                if (!cemNavController.popBackStack()) {
                    onExit()
                }
            }
        )
    }
}

fun NavGraphBuilder.cemOnboardingDestination(
    onNavigateBack: () -> Unit,
    onFinishOnboarding: () -> Unit
) {
    composable<CemOnboardingRoute> {
        val viewModel = hiltViewModel<CemOnboardingVM>()

        CemOnboardingScreen(
            onNavigateBack = onNavigateBack,
            onFinishOnboarding = {
                viewModel.finishOnboarding(onFinishOnboarding)
            }
        )
    }
}

fun NavGraphBuilder.cemCategoriesDestination(
    onUserPanel: () -> Unit,
    onCategoryClick: (CategoryUIM) -> Unit,
    onQuizClick: (CategoryUIM) -> Unit,
    onNavigateBack: () -> Unit
) {
    composable<CemCategoriesRoute> {
        val viewModel = hiltViewModel<CemCategoriesVM>()
        val state = viewModel.state.collectAsStateWithLifecycle()

        CemCategoriesScreen(
            state = state.value,
            onEvent = viewModel::onEvent,
            onNavigateBack = onNavigateBack,
            onUserPanel = onUserPanel,
            onCategoryClick = onCategoryClick,
            onQuizClick = onQuizClick
        )
    }
}

fun NavGraphBuilder.cemQuizDestination(
    onNavigateBack: () -> Unit
) {
    composable<CemQuizRoute> {
        val viewModel = hiltViewModel<CemQuizVM>()
        val state = viewModel.state.collectAsStateWithLifecycle()

        MMQuizScreen(
            state = state.value,
            onEvent = viewModel::onEvent,
            onNavigateBack = onNavigateBack
        )
    }
}
