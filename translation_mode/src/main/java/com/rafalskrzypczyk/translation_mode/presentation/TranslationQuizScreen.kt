package com.rafalskrzypczyk.translation_mode.presentation

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.BaseQuizScreen
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.ReportIssueDialog
import com.rafalskrzypczyk.core.composables.RotateDevicePrompt
import com.rafalskrzypczyk.core.utils.QuizSideEffect
import com.rafalskrzypczyk.translation_mode.presentation.components.TranslationQuizContent
import com.rafalskrzypczyk.translation_mode.presentation.components.TranslationQuizTopPanel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TranslationQuizScreen(
    state: TranslationQuizState,
    effect: SharedFlow<QuizSideEffect>,
    onEvent: (TranslationQuizEvents) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val successReportMsg = stringResource(com.rafalskrzypczyk.core.R.string.report_issue_success)

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(effect) {
        effect.collectLatest { sideEffect ->
            when (sideEffect) {
                QuizSideEffect.ShowReportSuccess -> {
                    Toast.makeText(context, successReportMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(state.isQuizFinished) {
        if (state.isQuizFinished) {
            keyboardController?.hide()
        }
    }

    BaseQuizScreen(
        title = stringResource(com.rafalskrzypczyk.core.R.string.title_translation_mode),
        quizTopPanel = { 
            TranslationQuizTopPanel(
                score = state.userScore, 
                correctAnswers = state.correctAnswersCount 
            ) 
        },
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
            description = state.reportIssueDescription,
            onDescriptionChanged = { description -> onEvent(TranslationQuizEvents.OnReportIssueDescriptionChanged(description)) },
            onDismiss = { onEvent(TranslationQuizEvents.ToggleReportDialog(false)) },
            onSend = { onEvent(TranslationQuizEvents.OnReportIssue) }
        )
    }
}
