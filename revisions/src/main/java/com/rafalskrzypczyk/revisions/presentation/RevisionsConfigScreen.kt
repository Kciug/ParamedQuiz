package com.rafalskrzypczyk.revisions.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.top_bars.NavTopBar
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.revisions.R
import com.rafalskrzypczyk.revisions.domain.models.RevisionCategory
import com.rafalskrzypczyk.revisions.domain.models.RevisionCriterion
import com.rafalskrzypczyk.revisions.presentation.config.RevisionsConfigState
import com.rafalskrzypczyk.revisions.presentation.config.RevisionsConfigUIEvents
import com.rafalskrzypczyk.revisions.presentation.config.components.CategorySelectionDialog
import com.rafalskrzypczyk.revisions.presentation.config.components.RevisionsConfigScreenContent
import com.rafalskrzypczyk.revisions.presentation.config.components.RevisionConfigDialog

@Composable
fun RevisionsConfigScreen(
    state: RevisionsConfigState,
    onEvent: (RevisionsConfigUIEvents) -> Unit,
    onNavigateBack: () -> Unit,
    onStartSession: (QuizMode, Long?, RevisionCriterion, Int?) -> Unit
) {
    Scaffold(
        topBar = {
            NavTopBar(
                title = stringResource(R.string.revisions_config_title),
                onNavigateBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
        ) {
            when (state.responseState) {
                ResponseState.Idle -> {}
                ResponseState.Loading -> Loading()
                is ResponseState.Error -> ErrorDialog(state.responseState.message) {
                    onNavigateBack()
                }
                ResponseState.Success -> {
                    RevisionsConfigScreenContent(
                        selectedMode = state.selectedMode,
                        onSelectMode = { onEvent(RevisionsConfigUIEvents.SelectMode(it)) }
                    )
                }
            }
        }
    }

    if (state.isConfigDialogVisible) {
        RevisionConfigDialog(
            state = state,
            onEvent = onEvent,
            onTriggerCategoryDialog = { onEvent(RevisionsConfigUIEvents.ShowCategoryDialog) },
            onStartSession = onStartSession,
            onDismiss = { onEvent(RevisionsConfigUIEvents.DismissConfigDialog) }
        )
    }

    if (state.isCategoryDialogVisible && state.categoriesList.isNotEmpty()) {
        CategorySelectionDialog(
            categories = state.categoriesList,
            selectedCategory = state.selectedCategory,
            onCategorySelected = { onEvent(RevisionsConfigUIEvents.SelectCategory(it)) },
            onDismiss = { onEvent(RevisionsConfigUIEvents.DismissCategoryDialog) }
        )
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun RevisionsConfigScreenPreview() {
    ParamedQuizTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            RevisionsConfigScreen(
                state = RevisionsConfigState(
                    selectedMode = QuizMode.MainMode,
                    categoriesList = listOf(
                        RevisionCategory(
                            id = 1L,
                            title = "Kardiologia",
                            mode = QuizMode.MainMode,
                            totalQuestionsCount = 50,
                            answeredQuestionsCount = 12,
                            isEligible = true
                        ),
                        RevisionCategory(
                            id = 2L,
                            title = "Pediatria",
                            mode = QuizMode.MainMode,
                            totalQuestionsCount = 30,
                            answeredQuestionsCount = 2,
                            isEligible = false
                        )
                    ),
                    selectedCategory = RevisionCategory(
                        id = 1L,
                        title = "Kardiologia",
                        mode = QuizMode.MainMode,
                        totalQuestionsCount = 50,
                        answeredQuestionsCount = 12,
                        isEligible = true
                    ),
                    selectedCriterion = RevisionCriterion.WORST,
                    selectedLimit = 10,
                    availableQuestionsCount = 12,
                    availableLimits = listOf(10, null),
                    responseState = ResponseState.Success,
                    isModeEligible = true
                ),
                onEvent = {},
                onNavigateBack = {},
                onStartSession = { _, _, _, _ -> }
            )
        }
    }
}
