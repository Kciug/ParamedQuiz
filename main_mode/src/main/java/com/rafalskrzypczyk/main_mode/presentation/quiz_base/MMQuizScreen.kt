package com.rafalskrzypczyk.main_mode.presentation.quiz_base

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.BaseQuizScreen
import com.rafalskrzypczyk.core.composables.CorrectAnswersLabel
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.PreviewContainer
import com.rafalskrzypczyk.core.composables.UserPointsLabel
import com.rafalskrzypczyk.main_mode.presentation.daily_exercise.DailyExerciseFinishedExtras
import kotlin.math.max

@Composable
fun MMQuizScreen(
    state: QuizState,
    onEvent: (MMQuizUIEvents) -> Unit,
    onNavigateBack: () -> Unit,
) {
    BaseQuizScreen(
        title = state.categoryTitle,
        quizTopPanel = { MMQuizTopPanel(score = state.userScore, correctAnswers = state.correctAnswers) },
        currentQuestionIndex = state.currentQuestionNumber,
        quizFinished = state.isQuizFinished,
        quizFinishedState = state.quizFinishedState,
        quizFinishedExtras = {
            if (state.isDailyExercise) {
                DailyExerciseFinishedExtras(
                    modifier = Modifier.padding(top = Dimens.DEFAULT_PADDING)
                )
            } else {
                val answeredCount = max(1, state.answeredQuestions.size)
                MMQuizFinishedExtras(
                    modifier = Modifier.padding(top = Dimens.DEFAULT_PADDING),
                    correctAnswers = state.correctAnswers,
                    totalQuestions = state.answeredQuestions.size,
                    averageResponseTimeMs = state.totalResponseTime / answeredCount,
                    totalDurationMs = state.totalResponseTime, 
                    averagePrecision = state.averagePrecision,
                    onReviewAnswersClick = { onEvent(MMQuizUIEvents.ToggleReviewDialog(true)) }
                )
            }
        },
        showBackConfirmation = state.showExitConfirmation,
        onBackAction = { onEvent(MMQuizUIEvents.OnBackPressed) },
        onBackDiscarded = { onEvent(MMQuizUIEvents.OnBackDiscarded) },
        onBackConfirmed = { onEvent(MMQuizUIEvents.OnBackConfirmed(onNavigateBack)) },
        onNavigateBack = onNavigateBack,
        onReportIssue = {}
    ) { innerPadding, titlePanel ->
        val modifier = Modifier
            .padding(
                top = innerPadding.calculateTopPadding(),
                start = innerPadding.calculateLeftPadding(LayoutDirection.Ltr),
                end = innerPadding.calculateRightPadding(LayoutDirection.Ltr),
            )

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
                    MMQuizScreenContent(
                        modifier = modifier,
                        scaffoldPadding = innerPadding,
                        state = state,
                        onEvent = onEvent
                    )
                }
            }
        }
    }
    
    if (state.showReviewDialog) {
        QuestionReviewDialog(
            questions = state.answeredQuestions,
            onDismiss = { onEvent(MMQuizUIEvents.ToggleReviewDialog(false)) }
        )
    }
}

@Composable
fun MMQuizTopPanel(
    modifier: Modifier = Modifier,
    score: Int,
    correctAnswers: Int,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        UserPointsLabel(value = score, grayOutWhenZero = false)
        CorrectAnswersLabel(value = correctAnswers)
    }
}

@Composable
fun MMQuizScreenContent(
    modifier: Modifier = Modifier,
    scaffoldPadding: PaddingValues,
    state: QuizState,
    onEvent: (MMQuizUIEvents) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(
            targetState = state.question,
            transitionSpec = {
                slideInHorizontally(
                    initialOffsetX = { it }
                ) togetherWith slideOutHorizontally(
                    targetOffsetX = { -it }
                )
            },
            label = "nextQuestionTransition",
            contentKey = { question -> question.id }
        ) { question ->
            QuizGameContent(
                scaffoldPadding = scaffoldPadding,
                question = question,
                onAnswerSelected = { answerId -> onEvent.invoke(MMQuizUIEvents.OnAnswerClicked(answerId)) },
                onSubmitAnswer = { onEvent.invoke(MMQuizUIEvents.OnSubmitAnswer) },
                onNextQuestion = { onEvent.invoke(MMQuizUIEvents.OnNextQuestion) }
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MMQuizScreenPreview() {
    val questionText = "Czy można tańcem wielbić Boga?"
    val questionAnswers = listOf(
        AnswerUIM(1, "A można, jak najbardziej", false),
        AnswerUIM(2, "Jeszcze jak!", false),
        AnswerUIM(3, "Coooo?", false),
        AnswerUIM(4, "Nie wiem", false)
    )

    val state = remember {
        mutableStateOf(
            QuizState(
                userScore = 13500,
                correctAnswers = 0,
                responseState = ResponseState.Success,
                categoryTitle = "Test",
                currentQuestionNumber = 1,
                questionsCount = 10,
                question = QuestionUIM(
                    questionText = questionText,
                    answers = questionAnswers,
                    correctAnswers = questionAnswers.filter { it.id == 2L }.map { it.answerText },
                )
            )
        )
    }

    PreviewContainer {
        MMQuizScreen(
            state = state.value,
            onEvent = { event ->
                when (event) {
                    is MMQuizUIEvents.OnAnswerClicked -> {
                        val updatedAnswers = state.value.question.answers.map { answer ->
                            if (answer.id == event.answerId) answer.copy(isSelected = !answer.isSelected)
                            else answer
                        }

                        state.value = state.value.copy(question = state.value.question.copy(answers = updatedAnswers))
                    }

                    MMQuizUIEvents.OnBackDiscarded -> {
                        state.value = state.value.copy(showExitConfirmation = false)
                    }

                    MMQuizUIEvents.OnBackPressed -> {
                        state.value = state.value.copy(showExitConfirmation = true)
                    }

                    MMQuizUIEvents.OnNextQuestion -> {
                        state.value = state.value.copy(
                            question = QuestionUIM(
                                questionText = questionText,
                                answers = questionAnswers,
                                isAnswerSubmitted = false
                            )
                        )
                    }

                    MMQuizUIEvents.OnSubmitAnswer -> {
                        val isAnswerCorrect = state.value.question.answers.find { it.isSelected }?.id == 2L

                        state.value = state.value.copy(
                            question = state.value.question.submitAnswer(
                                answeredCorrectly = isAnswerCorrect,
                                precision = 100
                            ),
                            correctAnswers = if(isAnswerCorrect) state.value.correctAnswers + 1 else state.value.correctAnswers,
                            userScore = if(isAnswerCorrect) state.value.userScore + 100 else state.value.userScore
                        )
                    }

                    is MMQuizUIEvents.OnBackConfirmed -> {}
                    is MMQuizUIEvents.ToggleReviewDialog -> {}
                }
            }
        ) { }
    }
}
