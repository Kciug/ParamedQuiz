package com.rafalskrzypczyk.main_mode.presentation.quiz_base

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.ConfirmationDialog
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.NavTopBar
import com.rafalskrzypczyk.core.composables.PreviewContainer
import com.rafalskrzypczyk.core.composables.QuizFinishScreen

@Composable
fun MMQuizScreen(
    state: QuizState,
    onEvent: (MMQuizUIEvents) -> Unit,
    onNavigateBack: () -> Unit,
) {
    BackHandler {
        onEvent.invoke(MMQuizUIEvents.OnBackPressed)
    }

    Scaffold (
        topBar = {
            NavTopBar(
                title = state.categoryTitle,
                onNavigateBack = { onEvent.invoke(MMQuizUIEvents.OnBackPressed) }
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

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
                    AnimatedContent(
                        targetState = state.isQuizFinished,
                        transitionSpec = {
                            scaleIn() togetherWith scaleOut()
                        },
                        label = "quizFinishedTransition"
                    ) { quizFinished ->
                        if(quizFinished) {
                            QuizFinishScreen(onNavigateBack = onNavigateBack)
                        } else {
                            MMQuizScreenContent(
                                modifier = modifier,
                                state = state,
                                onEvent = onEvent
                            )
                        }
                    }
                }
            }
        }

        if(state.showExitConfirmation) {
            ConfirmationDialog(
                title = stringResource(R.string.dialog_title_confirm_exit_quiz),
                onConfirm = { onNavigateBack() },
                onDismiss = { onEvent.invoke(MMQuizUIEvents.OnBackDiscarded) }
            )
        }
    }
}

@Composable
fun MMQuizScreenContent(
    modifier: Modifier = Modifier,
    state: QuizState,
    onEvent: (MMQuizUIEvents) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        QuizHeader(
            modifier = Modifier.padding(horizontal = Dimens.DEFAULT_PADDING),
            userScore = state.userScore,
            correctAnswers = state.correctAnswers,
            totalQuestions = state.questionsCount,
            currentQuestion = state.currentQuestionNumber
        )
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
    val questionText = "Czy można testować wszystko na produkcji?"
    val questionAnswers = listOf(
        AnswerUIM(1, "Błędna odpowiedź", false),
        AnswerUIM(2, "Poprawna odpowiedź", false),
        AnswerUIM(3, "Kolejna błędna odpowiedź", false),
        AnswerUIM(4, "Nie wiem", false)
    )

    var state = remember {
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
                                answeredCorrectly = isAnswerCorrect
                            ),
                            correctAnswers = if(isAnswerCorrect) state.value.correctAnswers + 1 else state.value.correctAnswers,
                            userScore = if(isAnswerCorrect) state.value.userScore + 100 else state.value.userScore
                        )
                    }
                }
            }
        ) { }
    }
}

