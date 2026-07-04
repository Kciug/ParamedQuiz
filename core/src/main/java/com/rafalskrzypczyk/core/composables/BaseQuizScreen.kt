package com.rafalskrzypczyk.core.composables

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.rounded.OutlinedFlag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.composables.quiz_finished.QuizFinishedScreen
import com.rafalskrzypczyk.core.composables.quiz_finished.QuizFinishedState
import com.rafalskrzypczyk.core.composables.top_bars.QuizTopBar

@Composable
fun BaseQuizScreen(
    title: String,
    quizTopPanel: @Composable () -> Unit = {},
    currentQuestionIndex: Int = 0,
    isMultipleChoice: Boolean = false,
    quizFinished: Boolean,
    waitingForAd: Boolean = false,
    quizFinishedState: QuizFinishedState,
    quizFinishedExtras: @Composable () -> Unit = {},
    showBackConfirmation: Boolean,
    showTopBar: Boolean = true,
    customBadgeText: String? = null,
    onBackAction: () -> Unit = {},
    onBackDiscarded: () -> Unit = {},
    onBackConfirmed: () -> Unit = {},
    onNavigateBack: () -> Unit,
    onReportIssue: () -> Unit,
    quizContent: @Composable (PaddingValues, @Composable () -> Unit) -> Unit,
) {
    val titlePanelConsumed = remember { mutableStateOf(false) }

    val defaultTitlePanel: @Composable () -> Unit = {
        BaseQuizTitlePanel(title, currentQuestionIndex, isMultipleChoice, customBadgeText)
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
                        if (showTopBar) {
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
    currentQuestionIndex: Int,
    isMultipleChoice: Boolean = false,
    customBadgeText: String? = null
) {
    Column {
        TextHeadline(title)
        if (customBadgeText != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(Dimens.RADIUS_SMALL),
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                ) {
                    TextPrimary(
                        text = customBadgeText,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 12.sp
                    )
                }
                if (isMultipleChoice) {
                    Spacer(modifier = Modifier.weight(1f))
                    MultipleChoiceBadge()
                }
            }
        } else if (currentQuestionIndex > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextPrimary(
                    text = stringResource(R.string.base_quiz_question_number, currentQuestionIndex),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
                if (isMultipleChoice) {
                    Spacer(modifier = Modifier.weight(1f))
                    MultipleChoiceBadge()
                }
            }
        }
    }
}

private object MultipleChoiceBadgeDefaults {
    val HorizontalPadding = 6.dp
    val VerticalPadding = 1.dp
    val Spacing = 4.dp
    val IconSize = 10.dp
    val Shape = RoundedCornerShape(Dimens.RADIUS_SMALL)
    const val BackgroundAlpha = 0.15f
}

@Composable
private fun MultipleChoiceBadge() {
    Surface(
        color = MaterialTheme.colorScheme.tertiary.copy(alpha = MultipleChoiceBadgeDefaults.BackgroundAlpha),
        shape = MultipleChoiceBadgeDefaults.Shape
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = MultipleChoiceBadgeDefaults.HorizontalPadding,
                vertical = MultipleChoiceBadgeDefaults.VerticalPadding
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MultipleChoiceBadgeDefaults.Spacing)
        ) {
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(MultipleChoiceBadgeDefaults.IconSize)
            )
            TextCaption(
                text = stringResource(R.string.badge_multiple_choice),
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold
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
            isMultipleChoice = true,
            onBackAction = {},
            onBackDiscarded = {},
            onBackConfirmed = {},
            onNavigateBack = {},
            onReportIssue = {}
        ) { _, _ ->

        }
    }
}