package com.rafalskrzypczyk.translation_mode.presentation

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.BaseQuizScreen
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.ReportIssueDialog
import com.rafalskrzypczyk.core.composables.TextFieldPrimary
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.composables.TextTitle
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.core.ui.theme.MQRed
import com.rafalskrzypczyk.translation_mode.domain.TranslationQuestionUIM

@Composable
fun TranslationQuizScreen(
    onNavigateBack: () -> Unit,
    viewModel: TranslationQuizViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    
    // Hardcoded string resource for now as I cannot easily add strings to core strings.xml without reading it first and parsing XML
    // Assuming usage of some common strings or English fallback
    val successReportMsg = "Report sent successfully" 

    LaunchedEffect(state.showReportSuccessToast) {
        if (state.showReportSuccessToast) {
            Toast.makeText(context, successReportMsg, Toast.LENGTH_SHORT).show()
        }
    }

    BaseQuizScreen(
        title = "Translations", // Hardcoded title
        currentQuestionIndex = state.currentQuestionIndex + 1, // 1-based index for display
        quizFinished = state.isQuizFinished,
        quizFinishedState = state.quizFinishedState,
        showBackConfirmation = state.showExitConfirmation,
        onBackAction = { viewModel.onEvent(TranslationQuizEvents.OnBackPressed) },
        onBackDiscarded = { viewModel.onEvent(TranslationQuizEvents.OnBackDiscarded) },
        onBackConfirmed = { viewModel.onEvent(TranslationQuizEvents.OnBackConfirmed(onNavigateBack)) },
        onNavigateBack = onNavigateBack,
        onReportIssue = { viewModel.onEvent(TranslationQuizEvents.ToggleReportDialog(true)) }
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
                        onEvent = viewModel::onEvent
                    )
                }
            }
        }
    }

    if (state.showReportDialog) {
        ReportIssueDialog(
            questionText = state.currentQuestion?.phrase ?: "",
            onDismiss = { viewModel.onEvent(TranslationQuizEvents.ToggleReportDialog(false)) },
            onSend = { description -> viewModel.onEvent(TranslationQuizEvents.OnReportIssue(description)) }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = Dimens.DEFAULT_PADDING)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        titlePanel()
        
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

        TextFieldPrimary(
            textValue = currentQuestion.userAnswer,
            onValueChange = { onEvent(TranslationQuizEvents.OnAnswerChanged(it)) },
            hint = "Your translation"
        )

        Spacer(modifier = Modifier.height(Dimens.LARGE_PADDING))

        if (!currentQuestion.isAnswered) {
            ButtonPrimary(
                title = "Check Answer",
                onClick = { onEvent(TranslationQuizEvents.OnSubmitAnswer) },
                enabled = currentQuestion.userAnswer.isNotBlank()
            )
        } else {
            FeedbackSection(question = currentQuestion)
            
            Spacer(modifier = Modifier.height(Dimens.LARGE_PADDING))
            
            ButtonPrimary(
                title = "Next Word",
                onClick = { onEvent(TranslationQuizEvents.OnNextQuestion) }
            )
        }
        
        Spacer(modifier = Modifier.height(Dimens.LARGE_PADDING))
    }
}

@Composable
fun FeedbackSection(question: TranslationQuestionUIM) {
    val backgroundColor = if (question.isCorrect) MQGreen.copy(alpha = 0.1f) else MQRed.copy(alpha = 0.1f)
    val borderColor = if (question.isCorrect) MQGreen else MQRed
    val feedbackText = if (question.isCorrect) "Correct!" else "Incorrect"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .padding(Dimens.DEFAULT_PADDING)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextHeadline(
                text = feedbackText,
                color = borderColor
            )
            
            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))
            
            TextPrimary(text = "Possible translations:")
            question.possibleTranslations.forEach { translation ->
                TextPrimary(
                    text = "• $translation"
                )
            }
        }
    }
}