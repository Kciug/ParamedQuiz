package com.rafalskrzypczyk.swipe_mode.presentation

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.BaseQuizScreen
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.PreviewContainer

@Composable
fun SwipeModeScreen(
    state: SwipeModeState,
    onEvent: (SwipeModeUIEvents) -> Unit,
    onNavigateBack: () -> Unit,
) {
    BaseQuizScreen(
        title = stringResource(com.rafalskrzypczyk.core.R.string.title_swipe_mode),
        quizTopPanel = { SwipeModeQuizPanel(score = state.userScore) },
        currentQuestionIndex = state.currentQuestionNumber,
        quizFinished = state.isQuizFinished,
        quizFinishedState = state.quizFinishedState,
        quizFinishedExtras = {
             SwipeModeFinishedExtras(
                 modifier = Modifier.padding(top = Dimens.DEFAULT_PADDING),
                 correctAnswers = state.correctAnswers,
                 totalQuestions = state.quizFinishedState.seenQuestions,
                 bestStreak = state.bestStreak,
                 averageResponseTimeMs = state.averageResponseTime,
                 totalDurationMs = state.totalQuizDuration,
                 type1Errors = state.type1Errors,
                 type2Errors = state.type2Errors
             )
        },
        showBackConfirmation = state.showExitConfirmation,
        onBackAction = { onEvent.invoke(SwipeModeUIEvents.OnBackPressed) },
        onBackDiscarded = { onEvent.invoke(SwipeModeUIEvents.OnBackDiscarded) },
        onBackConfirmed = { onEvent.invoke(SwipeModeUIEvents.OnBackConfirmed(onNavigateBack)) },
        onNavigateBack = onNavigateBack,
        onReportIssue = {}
    ) { paddingValues, titlePanel ->
        val modifier = Modifier.padding(paddingValues)

        AnimatedContent(
            targetState = state.responseState,
            transitionSpec = {
                scaleIn() togetherWith scaleOut()
            },
            label = "responseTransition"
        ) { responseState ->
            when(responseState) {
                ResponseState.Idle -> {}
                ResponseState.Loading -> Loading()
                is ResponseState.Error -> ErrorDialog(responseState.message) { onNavigateBack() }
                ResponseState.Success -> {
                    SwipeModeScreenContent(
                        modifier = modifier,
                        state = state,
                        onEvent = onEvent
                    )
                }
            }
        }
    }
}

@Composable
fun SwipeModeScreenContent(
    modifier: Modifier = Modifier,
    state: SwipeModeState,
    onEvent: (SwipeModeUIEvents) -> Unit
) {
    Column (
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            state.questionsPair.forEach {
                key(it.id) {
                    SwipeQuestionCard(
                        question = it,
                        horizontalPadding = Dimens.DEFAULT_PADDING
                    ) { questionId, isCorrect ->
                        onEvent.invoke(SwipeModeUIEvents.SubmitAnswer(questionId, isCorrect))
                    }
                }
            }
            AnswerResultIndicator(
                modifier = Modifier.align(Alignment.TopCenter),
                answerResult = state.answerResult
            )
        }
        SwipeStatsComponent(
            modifier = Modifier.padding(Dimens.DEFAULT_PADDING),
            correctAnswers = state.correctAnswers,
            currentStreak = state.currentStreak,
            bestStreak = state.bestStreak
        )
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun SwipeModeScreenPreview() {
    PreviewContainer {
        SwipeModeScreen(
            state = SwipeModeState(
                responseState = ResponseState.Success,
                questionsPair = listOf(
                    SwipeQuestionUIModel(0, "Przykładowe pytanie prawda/fałsz")
                )
            ),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}
