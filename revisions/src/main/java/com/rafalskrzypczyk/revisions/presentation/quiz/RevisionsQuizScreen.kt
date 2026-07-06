package com.rafalskrzypczyk.revisions.presentation.quiz

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import android.app.Activity
import com.rafalskrzypczyk.core.ads.AdManagerEntryPoint
import dagger.hilt.android.EntryPointAccessors
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.BaseQuizScreen
import com.rafalskrzypczyk.core.composables.CorrectAnswersLabel
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.ReportIssueDialog
import com.rafalskrzypczyk.core.composables.RotateDevicePrompt
import com.rafalskrzypczyk.core.composables.UserPointsLabel
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.main_mode.presentation.quiz_base.QuizGameContent
import com.rafalskrzypczyk.revisions.R
import com.rafalskrzypczyk.revisions.presentation.quiz.components.RevisionsQuizFinishedExtras
import com.rafalskrzypczyk.revisions.presentation.quiz.components.RevisionsReviewDialog
import com.rafalskrzypczyk.translation_mode.presentation.TranslationQuizEvents
import com.rafalskrzypczyk.translation_mode.presentation.TranslationQuizState
import com.rafalskrzypczyk.translation_mode.presentation.components.TranslationQuizContent

@Composable
fun RevisionsQuizScreen(
    state: RevisionsQuizState,
    onEvent: (RevisionsQuizUIEvents) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val adManager = remember {
        EntryPointAccessors.fromApplication(context, AdManagerEntryPoint::class.java).adManager()
    }

    LaunchedEffect(state.showAd) {
        if (state.showAd) {
            val activity = context as? Activity
            if (activity != null) {
                adManager.showInterstitial(
                    activity = activity,
                    onAdShown = { onEvent(RevisionsQuizUIEvents.OnAdShown) },
                    onAdDismissed = { onEvent(RevisionsQuizUIEvents.OnAdDismissed) }
                )
            } else {
                onEvent(RevisionsQuizUIEvents.OnAdDismissed)
            }
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val translationState = remember(state) {
        TranslationQuizState(
            responseState = state.responseState,
            questions = state.translationQuestions,
            currentQuestionIndex = state.currentTranslationQuestionIndex,
            userScore = state.userScore,
            correctAnswersCount = state.correctAnswersCount,
            isQuizFinished = state.quizFinished,
            quizFinishedState = state.quizFinishedState,
            showExitConfirmation = state.showExitConfirmation,
            showReviewDialog = state.showReviewDialog
        )
    }

    val modeTitle = when (state.mode) {
        QuizMode.MainMode -> "Powtórki: Tryb Główny"
        QuizMode.CemMode -> "Powtórki: Tryb CEM"
        QuizMode.TranslationMode -> "Powtórki: Tłumaczenia"
        else -> "Powtórki"
    }

    BaseQuizScreen(
        title = modeTitle,
        customBadgeText = if (state.isCorrection) stringResource(R.string.revisions_question_badge_fix_errors) else null,
        progress = state.progress,
        range = state.range,
        currentQuestionIndex = state.currentQuestionNumber,
        quizFinished = state.quizFinished,
        waitingForAd = state.showAd,
        quizFinishedState = state.quizFinishedState,
        showBackConfirmation = state.showExitConfirmation,
        onBackAction = { onEvent(RevisionsQuizUIEvents.OnBackPressed) },
        onBackDiscarded = { onEvent(RevisionsQuizUIEvents.OnBackDiscarded) },
        onBackConfirmed = { onEvent(RevisionsQuizUIEvents.OnBackConfirmed) },
        onNavigateBack = onNavigateBack,
        onReportIssue = { onEvent(RevisionsQuizUIEvents.ToggleReportDialog(true)) },
        quizTopPanel = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                UserPointsLabel(
                    value = state.userScore,
                    grayOutWhenZero = false
                )
                CorrectAnswersLabel(
                    value = state.correctAnswersCount,
                    grayOutWhenZero = false
                )
            }
        },
        quizFinishedExtras = {
            RevisionsQuizFinishedExtras(
                modeName = modeTitle,
                questions = state.originalQuestions,
                attemptedQuestionIds = state.attemptedQuestionIds,
                correctCount = state.quizFinishedState.correctAnswers,
                onReviewClick = { onEvent(RevisionsQuizUIEvents.ToggleReviewDialog(true)) },
                modifier = Modifier.padding(top = Dimens.DEFAULT_PADDING)
            )
        }
    ) { innerPadding, titlePanel ->

        AnimatedContent(
            targetState = state.responseState,
            transitionSpec = { scaleIn() togetherWith scaleOut() },
            label = "quizResponseTransition"
        ) { responseState ->
            when (responseState) {
                ResponseState.Idle -> {}
                ResponseState.Loading -> Loading()
                is ResponseState.Error -> ErrorDialog(responseState.message) { onNavigateBack() }
                ResponseState.Success -> {
                    if (isLandscape && state.mode == QuizMode.TranslationMode) {
                        RotateDevicePrompt(modifier = Modifier.padding(innerPadding))
                    } else if (state.mode == QuizMode.TranslationMode) {
                        TranslationQuizContent(
                            paddingValues = innerPadding,
                            titlePanel = titlePanel,
                            state = translationState,
                            onEvent = { event ->
                                when (event) {
                                    is TranslationQuizEvents.OnAnswerChanged -> onEvent(RevisionsQuizUIEvents.OnTranslationAnswerChanged(event.text))
                                    TranslationQuizEvents.OnSubmitAnswer -> onEvent(RevisionsQuizUIEvents.OnSubmitAnswer)
                                    TranslationQuizEvents.OnNextQuestion -> onEvent(RevisionsQuizUIEvents.OnNextQuestion)
                                    TranslationQuizEvents.OnBackPressed -> onEvent(RevisionsQuizUIEvents.OnBackPressed)
                                    TranslationQuizEvents.OnBackDiscarded -> onEvent(RevisionsQuizUIEvents.OnBackDiscarded)
                                    is TranslationQuizEvents.OnBackConfirmed -> onEvent(RevisionsQuizUIEvents.OnBackConfirmed)
                                    else -> {}
                                }
                            }
                        )
                    } else {
                        val currentQ = state.currentQuestionUIM
                        if (currentQ != null) {
                            AnimatedContent(
                                targetState = currentQ,
                                transitionSpec = {
                                    slideInHorizontally(initialOffsetX = { it }) togetherWith slideOutHorizontally(targetOffsetX = { -it })
                                },
                                label = "revisionsQuizQuestionTransition",
                                contentKey = { question -> question.id }
                            ) { question ->
                                QuizGameContent(
                                    modifier = Modifier.padding(
                                        top = innerPadding.calculateTopPadding(),
                                        start = innerPadding.calculateLeftPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
                                        end = innerPadding.calculateRightPadding(androidx.compose.ui.unit.LayoutDirection.Ltr)
                                    ),
                                    scaffoldPadding = innerPadding,
                                    titlePanel = titlePanel,
                                    question = question,
                                    onAnswerSelected = { onEvent(RevisionsQuizUIEvents.OnAnswerSelected(it)) },
                                    onSubmitAnswer = { onEvent(RevisionsQuizUIEvents.OnSubmitAnswer) },
                                    onNextQuestion = { onEvent(RevisionsQuizUIEvents.OnNextQuestion) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.showReviewDialog) {
        RevisionsReviewDialog(
            questions = state.originalQuestions,
            failedQuestionIds = state.failedQuestionIds,
            attemptedQuestionIds = state.attemptedQuestionIds,
            remainingQueueIds = state.remainingQueueIds,
            errorCounts = state.errorCounts,
            onDismiss = { onEvent(RevisionsQuizUIEvents.ToggleReviewDialog(false)) }
        )
    }

    if (state.showReportDialog) {
        val currentQuestionText = when (state.mode) {
            QuizMode.TranslationMode -> state.currentTranslationQuestionUIM?.phrase ?: ""
            else -> state.currentQuestionUIM?.questionText ?: ""
        }
        ReportIssueDialog(
            questionText = currentQuestionText,
            description = state.reportIssueDescription,
            onDescriptionChanged = { description -> onEvent(RevisionsQuizUIEvents.OnReportIssueDescriptionChanged(description)) },
            onDismiss = { onEvent(RevisionsQuizUIEvents.ToggleReportDialog(false)) },
            onSend = { onEvent(RevisionsQuizUIEvents.OnReportIssue) }
        )
    }
}
