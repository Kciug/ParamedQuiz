package com.rafalskrzypczyk.paramedquiz.navigation

import android.content.Intent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Any,
    isOnboarding: () -> Boolean,
    onFinishOnboarding: () -> Unit,
) {
    val context = LocalContext.current
    val privacyPolicyUrl = stringResource(com.rafalskrzypczyk.home.R.string.privacy_policy_url)
    NavHost(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        navController = navController,
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
        termsOfServiceDestination(
            onAccepted = { navController.navigateToMainMenu() },
            onNavigateBack = { navController.popBackStack() }
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
            onNavigateToSwipeMode = { isTrial -> navController.navigateToSwipeMode(isTrial) },
            onNavigateToTranslationMode = { navController.navigateToTranslationMode() },
            onNavigateToCemMode = { navController.navigateToCemMode() },
            onNavigateToStore = { navController.navigateToStore() },
            onNavigateToDev = { navController.navigateToDevOptions() },
            onNavigateToRevisionsMode = { navController.navigateToRevisionsMode() }
        )

        userPageDestination(
            onNavigateBack = { navController.popBackStack() },
            onUserSettings = { navController.navigateToUserSettings() },
            onSignup = { navController.navigateToSignup() }
        )

        userSettingsDestination(
            onNavigateBack = { navController.popBackStack() },
            onSignOut = { navController.navigateToSignup(popUpToUserPage = true) },
            onTermsOfService = { navController.navigateToTermsOfService(isMandatory = false) },
            onPrivacyPolicy = {
                val intent = Intent(Intent.ACTION_VIEW, privacyPolicyUrl.toUri())
                context.startActivity(intent)
            }
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

        cemModeDestination(
            onExit = { navController.popBackStack() },
            onUserPanel = { navController.navigateToUserPage() }
        )

        revisionsModeDestination(
            onNavigateBack = { navController.popBackStack() }
        )

        onboardingDestination(
            navigateToSignup = { navController.navigateToSignup() },
            onFinishOnboarding = {
                onFinishOnboarding()
            },
            onTermsOfService = { navController.navigateToTermsOfService(isMandatory = false) }
        )

        storeDestination(
            onNavigateBack = { navController.popBackStack() }
        )

        devDestination { navController.popBackStack() }
    }
}