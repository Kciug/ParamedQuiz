package com.rafalskrzypczyk.core.composables

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.OutlinedFlag
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.composables.quiz_finished.QuizFinishedScreen
import com.rafalskrzypczyk.core.composables.quiz_finished.QuizFinishedState
import com.rafalskrzypczyk.core.composables.top_bars.QuizTopBar

@Composable
fun BaseQuizScreen(
    title: String,
    quizTopPanel: @Composable () -> Unit = {},
    currentQuestionIndex: Int = 0,
    quizFinished: Boolean,
    waitingForAd: Boolean = false,
    quizFinishedState: QuizFinishedState,
    quizFinishedExtras: @Composable () -> Unit = {},
    showBackConfirmation: Boolean,
    onBackAction: () -> Unit = {},
    onBackDiscarded: () -> Unit = {},
    onBackConfirmed: () -> Unit = {},
    onNavigateBack: () -> Unit,
    onReportIssue: () -> Unit,
    quizContent: @Composable (PaddingValues, @Composable () -> Unit) -> Unit,
) {
    val titlePanelConsumed = remember { mutableStateOf(false) }

    val defaultTitlePanel: @Composable () -> Unit = {
        BaseQuizTitlePanel(title, currentQuestionIndex)
    }

    val consumableTitlePanel: @Composable () -> Unit = {
        titlePanelConsumed.value = true
        defaultTitlePanel()
    }

    BackHandler {
        if(quizFinished)
            onNavigateBack()
        else
            onBackAction()
    }

    val contentState = when {
        quizFinished -> QuizContentState.Finished
        waitingForAd -> QuizContentState.WaitingForAd
        else -> QuizContentState.Active
    }

    AnimatedContent(
        targetState = contentState,
        transitionSpec = {
            scaleIn() togetherWith scaleOut()
        },
        label = "quizFinishedTransition"
    ) { targetState ->
        when(targetState) {
            QuizContentState.Finished -> {
                QuizFinishedScreen(
                    state = quizFinishedState,
                    enterDelay = 0,
                    onNavigateBack = { onNavigateBack() }
                ) { quizFinishedExtras() }
            }
            QuizContentState.WaitingForAd -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                )
            }
            QuizContentState.Active -> {
                Scaffold(
                    contentWindowInsets = WindowInsets.safeDrawing,
                    topBar = {
                        QuizTopBar(
                            titlePanel = {
                                if (!titlePanelConsumed.value) {
                                    defaultTitlePanel()
                                }
                            },
                            quizPanel = { quizTopPanel() },
                            actions = { ReportAction { onReportIssue() } }
                        ) { onBackAction() }
                    }
                ) { innerPadding ->
                    quizContent(innerPadding, consumableTitlePanel)
                }
            }
        }
    }

    if(showBackConfirmation) {
        ConfirmationDialog(
            title = stringResource(R.string.dialog_title_confirm_exit_quiz),
            onConfirm = {
                onBackDiscarded()
                onBackConfirmed()
            },
            onDismiss = onBackDiscarded
        )
    }
}

@Composable
fun BaseQuizTitlePanel(
    title: String,
    currentQuestionIndex: Int
) {
    Column {
        TextHeadline(title)
        if (currentQuestionIndex > 0) {
            TextPrimary(
                text = stringResource(R.string.base_quiz_question_number, currentQuestionIndex),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun ReportAction(
    modifier: Modifier = Modifier,
    onAction: () -> Unit
) {
    ActionButton(
        modifier = modifier,
        icon = Icons.Rounded.OutlinedFlag,
        description = "Settings"
    ) { onAction() }
}

private enum class QuizContentState {
    Active,
    WaitingForAd,
    Finished
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BaseQuizScreenPreview() {
    PreviewContainer {
        BaseQuizScreen(
            title = "Test",
            currentQuestionIndex = 15,
            quizFinished = false,
            quizFinishedState = QuizFinishedState(),
            showBackConfirmation = false,
            onBackAction = {},
            onBackDiscarded = {},
            onBackConfirmed = {},
            onNavigateBack = {},
            onReportIssue = {}
        ) { _, _ ->

        }
    }
}