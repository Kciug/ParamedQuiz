package com.rafalskrzypczyk.translation_mode.presentation

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.BaseQuizScreen
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.ReportIssueDialog
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.composables.TextTitle
import com.rafalskrzypczyk.translation_mode.R
import com.rafalskrzypczyk.translation_mode.presentation.components.TranslationFeedbackPanel
import com.rafalskrzypczyk.translation_mode.presentation.components.TranslationInput

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import com.rafalskrzypczyk.core.composables.ActionButton
import com.rafalskrzypczyk.core.composables.CorrectAnswersLabel
import com.rafalskrzypczyk.core.composables.UserPointsLabel

import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import com.rafalskrzypczyk.core.composables.RotateDevicePrompt

@Composable
fun TranslationQuizScreen(
    state: TranslationQuizState,
    onEvent: (TranslationQuizEvents) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val successReportMsg = stringResource(com.rafalskrzypczyk.core.R.string.report_issue_success)

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(state.showReportSuccessToast) {
        if (state.showReportSuccessToast) {
            Toast.makeText(context, successReportMsg, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(state.isQuizFinished) {
        if (state.isQuizFinished) {
            keyboardController?.hide()
        }
    }

    BaseQuizScreen(
        title = stringResource(com.rafalskrzypczyk.core.R.string.title_translation_mode),
        quizTopPanel = { TranslationQuizTopPanel(score = state.userScore, correctAnswers = state.correctAnswersCount) },
        currentQuestionIndex = state.currentQuestionIndex + 1,
        quizFinished = state.isQuizFinished,
        quizFinishedState = state.quizFinishedState,
        showBackConfirmation = state.showExitConfirmation,
        onBackAction = { onEvent(TranslationQuizEvents.OnBackPressed) },
        onBackDiscarded = { onEvent(TranslationQuizEvents.OnBackDiscarded) },
        onBackConfirmed = { onEvent(TranslationQuizEvents.OnBackConfirmed(onNavigateBack)) },
        onNavigateBack = onNavigateBack,
        onReportIssue = { onEvent(TranslationQuizEvents.ToggleReportDialog(true)) },
        quizFinishedExtras = {
             TranslationQuizFinishedExtras(
                 questions = state.questions,
                 onReviewClick = { onEvent(TranslationQuizEvents.ToggleReviewDialog(true)) },
                 modifier = Modifier.padding(top = Dimens.DEFAULT_PADDING)
             )
        },
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
                    if (isLandscape) {
                        RotateDevicePrompt(modifier = Modifier.padding(innerPadding))
                    } else {
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
    }

    if (state.showReviewDialog) {
        TranslationReviewDialog(
            questions = state.questions,
            onDismiss = { onEvent(TranslationQuizEvents.ToggleReviewDialog(false)) }
        )
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
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(state.currentQuestionIndex, state.isQuizFinished) {
        if (!state.isQuizFinished) {
            focusRequester.requestFocus()
        }
    }

    AnimatedContent(
        targetState = state.currentQuestion,
        transitionSpec = {
            slideInHorizontally(initialOffsetX = { it }) togetherWith slideOutHorizontally(targetOffsetX = { -it })
        },
        label = "questionTransition",
        contentKey = { it?.id }
    ) { question ->
        if (question == null) return@AnimatedContent

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 100.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Dimens.DEFAULT_PADDING),
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
                        TextPrimary(
                            text = stringResource(R.string.translate_phrase_instruction),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))
                        TextTitle(text = question.phrase, textAlign = TextAlign.Center)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(Dimens.DEFAULT_PADDING),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
            ) {
                TranslationInput(
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    text = question.userAnswer,
                    onValueChange = { if (!question.isAnswered) onEvent(TranslationQuizEvents.OnAnswerChanged(it)) },
                    readOnly = false, // Keep editable to prevent keyboard closing
                    enabled = true,
                    imeAction = if (question.isAnswered) ImeAction.Next else ImeAction.Done,
                    onDone = {
                        if (!question.isAnswered) {
                            onEvent(TranslationQuizEvents.OnSubmitAnswer)
                        } else {
                            onEvent(TranslationQuizEvents.OnNextQuestion)
                        }
                    }
                )

                if (!question.isAnswered) {
                    ActionButton(
                        icon = Icons.Default.Check,
                        description = stringResource(R.string.btn_check_answer),
                        onClick = { onEvent(TranslationQuizEvents.OnSubmitAnswer) },
                        enabled = question.userAnswer.isNotBlank(),
                        showBackground = true
                    )
                }
            }

            AnimatedVisibility(
                visible = question.isAnswered,
                enter = slideInVertically(initialOffsetY = { it }),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                TranslationFeedbackPanel(
                    question = question,
                    onNext = { onEvent(TranslationQuizEvents.OnNextQuestion) },
                    bottomPadding = 0.dp
                )
            }
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