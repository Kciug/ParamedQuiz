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
    userLoggedInProvider: () -> Boolean
) {
    NavHost(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        navController = navController,
        startDestination = MainMenu,
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
        signupDestination(
            onExit = { navController.popBackStack() },
            onAuthenticated = { navController.navigateToUserPage() }
        )

        mainMenuDestination(
            onNavigateToUserPanel = {
                if (userLoggedInProvider()) navController.navigateToUserPage()
                else navController.navigateToSignup()
            },
            onNavigateToMainMode = { navController.navigateToMainMode() }
        )

        userPageDestination(
            onNavigateBack = { navController.popBackStack() },
            onUserSettings = { navController.navigateToUserSettings() },
        )

        userSettingsDestination(
            onNavigateBack = { navController.popBackStack() },
            onSignOut = { navController.navigateToSignup() },
        )

        mainModeDestination(
            onExit = { navController.popBackStack() },
            onUserPanel = {
                if (userLoggedInProvider()) navController.navigateToUserPage()
                else navController.navigateToSignup()
            }
        )
    }
}