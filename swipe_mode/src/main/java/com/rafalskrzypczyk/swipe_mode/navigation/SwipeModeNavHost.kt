package com.rafalskrzypczyk.swipe_mode.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rafalskrzypczyk.swipe_mode.presentation.SwipeModeScreen
import com.rafalskrzypczyk.swipe_mode.presentation.SwipeModeVM
import com.rafalskrzypczyk.swipe_mode.presentation.onboarding.SwipeModeOnboardingScreen
import com.rafalskrzypczyk.swipe_mode.presentation.onboarding.SwipeModeOnboardingVM
import kotlinx.serialization.Serializable

@Composable
fun SwipeModeNavHost(
    onExit: () -> Unit,
    showOnboarding: Boolean
) {
    val navController = rememberNavController()
    val startDest: Any = remember { if (showOnboarding) Onboarding else SwipeQuiz }

    NavHost(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        navController = navController,
        startDestination = startDest,
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
        composable<Onboarding> {
            val viewModel = hiltViewModel<SwipeModeOnboardingVM>()
            SwipeModeOnboardingScreen(
                onNavigateBack = onExit,
                onFinishOnboarding = {
                    viewModel.finishOnboarding {
                        navController.navigate(SwipeQuiz) {
                            popUpTo(Onboarding) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable<SwipeQuiz> {
            val viewModel = hiltViewModel<SwipeModeVM>()
            val state = viewModel.state.collectAsStateWithLifecycle()

            SwipeModeScreen(
                state = state.value,
                onEvent = viewModel::onEvent,
                onNavigateBack = onExit
            )
        }
    }
}

@Serializable
object Onboarding

@Serializable
object SwipeQuiz
