package com.rafalskrzypczyk.revisions.presentation.quiz

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.BaseQuizScreen
import com.rafalskrzypczyk.core.composables.CorrectAnswersLabel
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.ReportIssueDialog
import com.rafalskrzypczyk.core.composables.RotateDevicePrompt
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.composables.UserPointsLabel
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.core.ui.theme.MQRed
import com.rafalskrzypczyk.core.ui.theme.MQYellow
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.main_mode.presentation.quiz_base.QuizGameContent
import com.rafalskrzypczyk.revisions.R
import com.rafalskrzypczyk.revisions.domain.models.RevisionQuestion
import com.rafalskrzypczyk.translation_mode.presentation.TranslationQuizEvents
import com.rafalskrzypczyk.translation_mode.presentation.TranslationQuizState
import com.rafalskrzypczyk.translation_mode.presentation.components.TranslationQuizContent

@Composable
fun RevisionsQuizScreen(
    state: RevisionsQuizState,
    onEvent: (RevisionsQuizUIEvents) -> Unit,
    onNavigateBack: () -> Unit
) {
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
        currentQuestionIndex = state.currentQuestionNumber,
        quizFinished = state.quizFinished,
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

@Composable
private fun RevisionsQuizFinishedExtras(
    modeName: String,
    questions: List<RevisionQuestion>,
    attemptedQuestionIds: Set<Long>,
    correctCount: Int,
    onReviewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val attemptedCount = questions.count { attemptedQuestionIds.contains(it.id) }
    val accuracy = if (attemptedCount > 0) ((correctCount.toFloat() / attemptedCount) * 100).toInt() else 0

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = Dimens.ELEVATION
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.DEFAULT_PADDING),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
        ) {
            TextHeadline(
                text = modeName,
                color = MaterialTheme.colorScheme.onSurface
            )

            TextPrimary(
                text = stringResource(R.string.revisions_quiz_finished_accuracy, accuracy),
                color = if (accuracy >= 50) MQGreen else MQRed
            )

            if (attemptedCount > 0) {
                Button(
                    onClick = onReviewClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.RADIUS_SMALL)
                ) {
                    Text(text = stringResource(R.string.revisions_quiz_finished_review_btn))
                }
            }
        }
    }
}

@Composable
private fun RevisionsReviewDialog(
    questions: List<RevisionQuestion>,
    failedQuestionIds: Set<Long>,
    attemptedQuestionIds: Set<Long>,
    remainingQueueIds: Set<Long>,
    onDismiss: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    val screenHeight = windowInfo.containerDpSize.height
    val reviewedQuestions = remember(questions, attemptedQuestionIds) {
        questions.filter { attemptedQuestionIds.contains(it.id) }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.DEFAULT_PADDING)
                .heightIn(max = screenHeight * 0.85f),
            shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(Dimens.DEFAULT_PADDING)
            ) {
                TextHeadline(
                    text = stringResource(R.string.revisions_review_title),
                    modifier = Modifier.padding(bottom = Dimens.ELEMENTS_SPACING)
                )
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
                ) {
                    items(reviewedQuestions) { question ->
                        val isFailed = failedQuestionIds.contains(question.id)
                        val isUnfinished = remainingQueueIds.contains(question.id)
                        RevisionsReviewCard(
                            question = question,
                            isFailed = isFailed,
                            isUnfinished = isUnfinished
                        )
                    }
                }
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.ELEMENTS_SPACING)
                ) {
                    Text(text = stringResource(com.rafalskrzypczyk.core.R.string.btn_confirm_OK))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RevisionsReviewCard(
    question: RevisionQuestion,
    isFailed: Boolean,
    isUnfinished: Boolean
) {
    val statusColor = when {
        isFailed -> MQRed
        isUnfinished -> MQYellow
        else -> MQGreen
    }
    val statusIcon = when {
        isFailed -> Icons.Outlined.Close
        isUnfinished -> Icons.Outlined.Warning
        else -> Icons.Outlined.Check
    }
    val statusLabel = when {
        isFailed -> stringResource(R.string.revisions_review_failed_limit)
        isUnfinished -> stringResource(R.string.revisions_review_unfinished)
        else -> stringResource(R.string.revisions_review_correct)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.DEFAULT_PADDING)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextHeadline(
                    text = question.text,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))

            Surface(
                shape = RoundedCornerShape(Dimens.RADIUS_SMALL),
                color = statusColor.copy(alpha = 0.15f)
            ) {
                TextPrimary(
                    text = statusLabel,
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))

            when (question) {
                is RevisionQuestion.Main -> {
                    TextCaption(text = stringResource(R.string.revisions_review_correct_answers))
                    Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
                    val correctTexts = question.question.answers.filter { it.isCorrect }.map { it.answerText }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        correctTexts.forEach { text ->
                            TextPrimary(text = "• $text", fontWeight = FontWeight.Bold, color = MQGreen)
                        }
                    }
                }
                is RevisionQuestion.Cem -> {
                    TextCaption(text = stringResource(R.string.revisions_review_correct_answers))
                    Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
                    val correctTexts = question.question.answers.filter { it.isCorrect }.map { it.answerText }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        correctTexts.forEach { text ->
                            TextPrimary(text = "• $text", fontWeight = FontWeight.Bold, color = MQGreen)
                        }
                    }
                }
                is RevisionQuestion.Translation -> {
                    TextCaption(text = stringResource(R.string.revisions_review_correct_answers))
                    Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        question.question.translations.forEach { translation ->
                            Surface(
                                shape = RoundedCornerShape(Dimens.RADIUS_INNER_DEFAULT),
                                color = MQGreen.copy(alpha = 0.15f)
                            ) {
                                TextPrimary(
                                    text = translation,
                                    color = MQGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
