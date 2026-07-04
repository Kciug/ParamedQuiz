package com.rafalskrzypczyk.revisions.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rafalskrzypczyk.revisions.presentation.RevisionsConfigScreen
import kotlinx.serialization.Serializable

@Serializable
internal object RevisionsConfigRoute

@Composable
fun RevisionsModeNavHost(
    onExit: () -> Unit
) {
    val revisionsNavController = rememberNavController()

    NavHost(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        navController = revisionsNavController,
        startDestination = RevisionsConfigRoute,
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
        revisionsConfigDestination(
            onNavigateBack = {
                if (!revisionsNavController.popBackStack()) {
                    onExit()
                }
            }
        )
    }
}

fun NavGraphBuilder.revisionsConfigDestination(
    onNavigateBack: () -> Unit
) {
    composable<RevisionsConfigRoute> {
        RevisionsConfigScreen(
            onNavigateBack = onNavigateBack
        )
    }
}
