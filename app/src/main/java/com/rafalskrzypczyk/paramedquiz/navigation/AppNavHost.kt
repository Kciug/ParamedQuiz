package com.rafalskrzypczyk.paramedquiz.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun AppNavHost(
    navController: NavHostController,
    isOnboarding: () -> Boolean,
    onFinishOnboarding: () -> Unit,
    onDataLoaded: () -> Unit,
) {
    NavHost(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        navController = navController,
        startDestination = Splash,
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
        splashDestination(
            onNavigateToOnboarding = {
                onDataLoaded()
                navController.navigate(Onboarding) { popUpTo(Splash) { inclusive = true } }
            },
            onNavigateToTerms = {
                onDataLoaded()
                navController.navigateToTermsOfService()
            },
            onNavigateToMain = {
                onDataLoaded()
                navController.navigateToMainMenu()
            }
        )

        termsOfServiceDestination(
            onAccepted = { navController.navigateToMainMenu() }
        )

        signupDestination(
            onExit = { navController.popBackStack() },
            onAuthenticated = {
                if (isOnboarding()) { navController.popBackStack() }
                else { navController.navigateToUserPage() }
            }
        )

        dailyExerciseDestination(
            onNavigateBack = { navController.popBackStack() }
        )

        mainMenuDestination(
            onNavigateToUserPanel = { navController.navigateToUserPage() },
            onNavigateToDailyExercise = { navController.navigateToDailyExercise() },
            onNavigateToMainMode = { navController.navigateToMainMode() },
            onNavigateToSwipeMode = { navController.navigateToSwipeMode() },
            onNavigateToTranslationMode = { navController.navigateToTranslationMode() },
            onNavigateToDev = { navController.navigateToDevOptions()
            }
        )

        userPageDestination(
            onNavigateBack = { navController.popBackStack() },
            onUserSettings = { navController.navigateToUserSettings() },
            onSignup = { navController.navigateToSignup() }
        )

        userSettingsDestination(
            onNavigateBack = { navController.popBackStack() },
            onSignOut = { navController.navigateToSignup(popUpToUserPage = true) },
        )

        mainModeDestination(
            onExit = { navController.popBackStack() },
            onUserPanel = { navController.navigateToUserPage() }
        )

        swipeModeDestination(
            onNavigateBack = { navController.popBackStack() }
        )

        translationModeDestination(
            onNavigateBack = { navController.popBackStack() }
        )

        onboardingDestination(
            navigateToSignup = { navController.navigateToSignup() },
            onFinishOnboarding = {
                onFinishOnboarding()
                navController.navigate(Splash) {
                    popUpTo(Onboarding) { inclusive = true }
                }
            }
        )

        devDestination { navController.popBackStack() }
    }
}