package com.rafalskrzypczyk.revisions.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.revisions.domain.models.RevisionCriterion
import com.rafalskrzypczyk.revisions.presentation.RevisionsConfigScreen
import com.rafalskrzypczyk.revisions.presentation.config.RevisionsConfigVM
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
            },
            onStartSession = { mode, categoryId, criterion, limit ->
                // TODO: navigate to revisions quiz screen
            }
        )
    }
}

fun NavGraphBuilder.revisionsConfigDestination(
    onNavigateBack: () -> Unit,
    onStartSession: (QuizMode, Long?, RevisionCriterion, Int?) -> Unit
) {
    composable<RevisionsConfigRoute> {
        val viewModel: RevisionsConfigVM = hiltViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        RevisionsConfigScreen(
            state = state,
            onEvent = viewModel::onEvent,
            onNavigateBack = onNavigateBack,
            onStartSession = onStartSession
        )
    }
}
