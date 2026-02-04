package com.rafalskrzypczyk.main_mode.navigation

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
import com.rafalskrzypczyk.main_mode.presentation.categories_screen.MMCategoriesScreen
import com.rafalskrzypczyk.main_mode.presentation.categories_screen.MMCategoriesVM
import com.rafalskrzypczyk.main_mode.presentation.onboarding.MainModeOnboardingScreen
import com.rafalskrzypczyk.main_mode.presentation.onboarding.MainModeOnboardingVM
import com.rafalskrzypczyk.main_mode.presentation.quiz_base.MMQuizScreen
import com.rafalskrzypczyk.main_mode.presentation.quiz_screen.MMQuizVM
import kotlinx.serialization.Serializable

@Composable
fun MainModeNavHost(
    onExit: () -> Unit,
    onUserPanel: () -> Unit,
    showOnboarding: Boolean
) {
    val mainModeNavController = rememberNavController()
    val startDest: Any = if (showOnboarding) Onboarding else Categories

    NavHost(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        navController = mainModeNavController,
        startDestination = startDest,
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
        mainModeOnboardingDestination(
            onNavigateBack = onExit,
            onFinishOnboarding = {
                mainModeNavController.navigate(Categories) {
                    popUpTo(Onboarding) { inclusive = true }
                }
            }
        )
        mainModeCategoriesDestination(
            onExit = onExit,
            onUserPanel = onUserPanel,
            onStartCategory = { categoryId, categoryTitle -> 
                mainModeNavController.navigateToQuiz(categoryId, categoryTitle)
            }
        )
        mainModeQuizDestination(
            onNavigateBack = { mainModeNavController.popBackStack() }
        )
    }
}

@Serializable
object Categories

@Serializable
object Onboarding

fun NavGraphBuilder.mainModeOnboardingDestination(
    onNavigateBack: () -> Unit,
    onFinishOnboarding: () -> Unit
) {
    composable<Onboarding> {
        val viewModel = hiltViewModel<MainModeOnboardingVM>()
        
        MainModeOnboardingScreen(
            onNavigateBack = onNavigateBack,
            onFinishOnboarding = {
                viewModel.finishOnboarding(onSuccess = onFinishOnboarding)
            }
        )
    }
}

fun NavGraphBuilder.mainModeCategoriesDestination(
    onExit: () -> Unit,
    onUserPanel: () -> Unit,
    onStartCategory: (Long, String) -> Unit
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

@Serializable
internal data class Quiz(
    val categoryId: Long,
    val categoryTitle: String
)

fun NavGraphBuilder.mainModeQuizDestination(
    onNavigateBack: () -> Unit,
) {
    composable<Quiz> {
        val viewModel = hiltViewModel<MMQuizVM>()
        val state = viewModel.state.collectAsStateWithLifecycle()

        MMQuizScreen(
            state = state.value,
            onEvent = viewModel::onEvent,
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavController.navigateToQuiz(categoryId: Long, categoryTitle: String) {
    navigate(route = Quiz(categoryId, categoryTitle))
}