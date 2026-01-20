package com.rafalskrzypczyk.translation_mode.presentation

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.BaseQuizScreen
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.ReportIssueDialog
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.composables.TextTitle
import com.rafalskrzypczyk.translation_mode.presentation.components.TranslationFeedbackPanel
import com.rafalskrzypczyk.translation_mode.presentation.components.TranslationInput

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import com.rafalskrzypczyk.core.composables.CorrectAnswersLabel
import com.rafalskrzypczyk.core.composables.UserPointsLabel

@Composable
fun TranslationQuizScreen(
    state: TranslationQuizState,
    onEvent: (TranslationQuizEvents) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val successReportMsg = "Report sent successfully"

    LaunchedEffect(state.showReportSuccessToast) {
        if (state.showReportSuccessToast) {
            Toast.makeText(context, successReportMsg, Toast.LENGTH_SHORT).show()
        }
    }

    BaseQuizScreen(
        title = "Translations",
        quizTopPanel = { TranslationQuizTopPanel(score = state.userScore, correctAnswers = state.correctAnswersCount) },
        currentQuestionIndex = state.currentQuestionIndex + 1,
        quizFinished = state.isQuizFinished,
        quizFinishedState = state.quizFinishedState,
        showBackConfirmation = state.showExitConfirmation,
        onBackAction = { onEvent(TranslationQuizEvents.OnBackPressed) },
        onBackDiscarded = { onEvent(TranslationQuizEvents.OnBackDiscarded) },
        onBackConfirmed = { onEvent(TranslationQuizEvents.OnBackConfirmed(onNavigateBack)) },
        onNavigateBack = onNavigateBack,
        onReportIssue = { onEvent(TranslationQuizEvents.ToggleReportDialog(true)) }
    ) { innerPadding, titlePanel ->
        
        AnimatedContent(
            targetState = state.responseState,
            transitionSpec = { scaleIn() togetherWith scaleOut() },
            label = "responseTransition"
        ) { responseState ->
            when (responseState) {
                ResponseState.Idle -> {}
                ResponseState.Loading -> Loading()
                is ResponseState.Error -> ErrorDialog(responseState.message) { onNavigateBack() }
                ResponseState.Success -> {
                    TranslationQuizContent(
                        paddingValues = innerPadding,
                        titlePanel = titlePanel,
                        state = state,
                        onEvent = onEvent
                    )
                }
            }
        }
    }

    if (state.showReportDialog) {
        ReportIssueDialog(
            questionText = state.currentQuestion?.phrase ?: "",
            onDismiss = { onEvent(TranslationQuizEvents.ToggleReportDialog(false)) },
            onSend = { description -> onEvent(TranslationQuizEvents.OnReportIssue(description)) }
        )
    }
}

@Composable
fun TranslationQuizContent(
    paddingValues: PaddingValues,
    titlePanel: @Composable () -> Unit,
    state: TranslationQuizState,
    onEvent: (TranslationQuizEvents) -> Unit
) {
    val currentQuestion = state.currentQuestion ?: return
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimens.DEFAULT_PADDING)
                .padding(bottom = Dimens.DEFAULT_PADDING * 5)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                titlePanel()
            }
            
            Spacer(modifier = Modifier.height(Dimens.LARGE_PADDING))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.DEFAULT_PADDING),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextPrimary(text = "Translate this phrase:", textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))
                    TextTitle(text = currentQuestion.phrase, textAlign = TextAlign.Center)
                }
            }

            Spacer(modifier = Modifier.height(Dimens.LARGE_PADDING))

            TranslationInput(
                text = currentQuestion.userAnswer,
                onValueChange = { onEvent(TranslationQuizEvents.OnAnswerChanged(it)) },
                enabled = !currentQuestion.isAnswered,
                onDone = {
                    keyboardController?.hide()
                    onEvent(TranslationQuizEvents.OnSubmitAnswer)
                }
            )
        }

        if (!currentQuestion.isAnswered) {
            ButtonPrimary(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.DEFAULT_PADDING),
                title = "Check Answer",
                onClick = {
                    keyboardController?.hide()
                    onEvent(TranslationQuizEvents.OnSubmitAnswer)
                },
                enabled = currentQuestion.userAnswer.isNotBlank()
            )
        }

        AnimatedVisibility(
            visible = currentQuestion.isAnswered,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            TranslationFeedbackPanel(
                question = currentQuestion,
                onNext = { onEvent(TranslationQuizEvents.OnNextQuestion) }
            )
        }
    }
}

@Composable
fun TranslationQuizTopPanel(
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