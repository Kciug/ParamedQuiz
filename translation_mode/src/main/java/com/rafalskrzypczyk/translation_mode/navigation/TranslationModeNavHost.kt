package com.rafalskrzypczyk.translation_mode.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rafalskrzypczyk.translation_mode.presentation.TranslationQuizScreen
import com.rafalskrzypczyk.translation_mode.presentation.TranslationQuizViewModel
import com.rafalskrzypczyk.translation_mode.presentation.onboarding.TranslationModeOnboardingScreen
import com.rafalskrzypczyk.translation_mode.presentation.onboarding.TranslationModeOnboardingVM
import kotlinx.serialization.Serializable

@Composable
fun TranslationModeNavHost(
    onExit: () -> Unit,
    showOnboarding: Boolean
) {
    val navController = rememberNavController()
    val startDest: Any = if (showOnboarding) Onboarding else TranslationQuiz

    NavHost(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        navController = navController,
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
        composable<Onboarding> {
            val viewModel = hiltViewModel<TranslationModeOnboardingVM>()
            TranslationModeOnboardingScreen(
                onNavigateBack = onExit,
                onFinishOnboarding = {
                    viewModel.finishOnboarding {
                        navController.navigate(TranslationQuiz) {
                            popUpTo(Onboarding) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable<TranslationQuiz> {
            val viewModel = hiltViewModel<TranslationQuizViewModel>()
            val state = viewModel.state.collectAsStateWithLifecycle()

            TranslationQuizScreen(
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
object TranslationQuiz
