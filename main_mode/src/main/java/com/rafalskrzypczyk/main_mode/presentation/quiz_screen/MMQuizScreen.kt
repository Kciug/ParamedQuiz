package com.rafalskrzypczyk.main_mode.presentation.quiz_screen

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.ButtonSecondary
import com.rafalskrzypczyk.core.composables.ConfirmationDialog
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.QuizLinearProgressBar
import com.rafalskrzypczyk.core.ui.NavigationTopBar
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.main_mode.R

@Composable
fun MMQuizScreen(
    state: MMQuizState,
    onEvent: (MMQuizUIEvents) -> Unit,
    onNavigateBack: () -> Unit,
) {
    BackHandler {
        onEvent.invoke(MMQuizUIEvents.OnBackPressed)
    }

    Scaffold (
        topBar = {
            NavigationTopBar(
                title = state.categoryTitle,
                onNavigateBack = { onEvent.invoke(MMQuizUIEvents.OnBackPressed) }
            )
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        when(state.responseState) {
            ResponseState.Idle -> {}
            ResponseState.Loading -> Loading()
            is ResponseState.Error -> ErrorDialog(state.responseState.message) { onNavigateBack() }
            ResponseState.Success -> {
                Box {
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(horizontal = Dimens.DEFAULT_PADDING)
                            .padding(bottom = Dimens.DEFAULT_PADDING),
                    ) {
                        QuizQuestionSection(
                            questionNumber = state.currentQuestionNumber,
                            questionsCount = state.questionsCount,
                            questionText = state.questionText
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        AnswersSection(
                            answers = state.answers,
                            onAnswerSelected = { answer ->
                                onEvent.invoke(
                                    MMQuizUIEvents.OnAnswerClicked(
                                        answer
                                    )
                                )
                            },
                            isSubmitted = state.isAnswerSubmitted,
                        )
                        Spacer(modifier = Modifier.height(Dimens.DEFAULT_PADDING))
                        ButtonPrimary(
                            title = stringResource(R.string.btn_submit),
                            onClick = { onEvent.invoke(MMQuizUIEvents.OnSubmitAnswer) },
                        )
                    }
                    AnimatedVisibility(
                        visible = state.isAnswerSubmitted,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it }),
                        modifier = modifier
                            .align(Alignment.BottomCenter)
                            .zIndex(1f),
                    ) {
                        SubmitSection(
                            isAnswerCorrect = state.isAnswerCorrect,
                            correctAnswers = state.correctAnswers,
                            onNextQuestion = { onEvent.invoke(MMQuizUIEvents.OnNextQuestion) }
                        )
                    }
                }
            }
        }

        if(state.isQuizFinished) {
            QuizFinishScreen(onNavigateBack = onNavigateBack)
        }

        if(state.showExitConfirmation) {
            ConfirmationDialog(
                title = stringResource(R.string.title_confirmation_exit_quiz),
                onConfirm = { onNavigateBack() },
                onDismiss = { onEvent.invoke(MMQuizUIEvents.OnBackDiscarded) }
            )
        }
    }
}

@Composable
private fun QuizQuestionSection(
    modifier: Modifier = Modifier,
    questionNumber: Int,
    questionsCount: Int,
    questionText: String,
) {
    Column (horizontalAlignment = Alignment.CenterHorizontally) {
        QuizLinearProgressBar(progress = questionNumber, range = questionsCount)
        Spacer(modifier = modifier.height(Dimens.DEFAULT_PADDING))
        Text(
            text = questionText,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AnswersSection(
    modifier: Modifier = Modifier,
    answers: List<AnswerUIM>,
    onAnswerSelected: (Long) -> Unit,
    isSubmitted: Boolean,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Dimens.DEFAULT_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        answers.forEach {
            AnswerItem(
                answer = it,
                isSubmitted = isSubmitted,
                onSelected = onAnswerSelected
            )
        }
    }
}

@Composable
fun AnswerItem(answer: AnswerUIM, isSubmitted: Boolean, onSelected: (Long) -> Unit) {
    val answerColor: Color = if(answer.isSelected) Color.Green else MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .defaultMinSize(minHeight = 70.dp)
            .border(
                width = Dimens.OUTLINE_THICKNESS,
                color = answerColor,
                shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT)
            )
            .clip(shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT))
            .clickable(enabled = !isSubmitted) { onSelected(answer.id) }
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        Text(
            text = answer.answerText,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SubmitSection(
    isAnswerCorrect: Boolean,
    correctAnswers: List<String>,
    onNextQuestion: () -> Unit
) {
    val verifiedIcon = if(isAnswerCorrect) Icons.Outlined.CheckCircleOutline else Icons.Outlined.Cancel
    val verifiedText = stringResource(if(isAnswerCorrect) R.string.answer_correct else R.string.answer_incorrect)
    val verifiedColor = if(isAnswerCorrect) Color.Green else Color.Red

    Column(modifier = Modifier
        .fillMaxWidth()
        .clip(shape = RoundedCornerShape(topStart = Dimens.RADIUS_SMALL, topEnd = Dimens.RADIUS_SMALL))
        .background(MaterialTheme.colorScheme.surfaceContainer)
        .padding(Dimens.DEFAULT_PADDING)
    ) {
        Row {
            Icon(
                imageVector = verifiedIcon,
                contentDescription = stringResource(R.string.ic_desc_answer_correctness),
                tint = verifiedColor
            )
            Text(
                text = verifiedText,
                color = verifiedColor,
                modifier = Modifier.padding(start = Dimens.SMALL_PADDING)
            )
        }

        if (!isAnswerCorrect) {
            Spacer(modifier = Modifier.height(Dimens.DEFAULT_PADDING))
            Text(text = stringResource(
                if(correctAnswers.count() > 1) R.string.question_correct_answers_plural
                else R.string.question_correct_answers_single
            ))
            correctAnswers.forEach {
                Text(
                    modifier = Modifier
                        .padding(Dimens.SMALL_PADDING)
                        .clip(shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(vertical = Dimens.SMALL_PADDING)
                        .padding(horizontal = Dimens.DEFAULT_PADDING),
                    text = it,
                    color = Color.Green
                )
            }
        }
        Spacer(modifier = Modifier.height(Dimens.DEFAULT_PADDING))
        ButtonPrimary(
            title = stringResource(R.string.btn_next_question),
            onClick = onNextQuestion
        )
    }
}

@Composable
private fun QuizFinishScreen(
    onNavigateBack: () -> Unit
) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Dimens.DEFAULT_PADDING),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Icon(
            imageVector = Icons.Default.SportsScore,
            contentDescription = stringResource(R.string.ic_desc_category_finished),
            tint = Color.Green,
            modifier = Modifier.size(Dimens.SIZE_MEDIUM)
        )
        Text(
            text = stringResource(R.string.category_finished_title),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.category_finished_message),
            textAlign = TextAlign.Center,
        )
        ButtonSecondary(
            title = stringResource(R.string.btn_back),
            onClick = onNavigateBack
        )
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

    var state = remember { mutableStateOf(MMQuizState(
        responseState = ResponseState.Success,
        categoryTitle = "Test",
        currentQuestionNumber = 1,
        questionsCount = 10,
        questionText = questionText,
        answers = questionAnswers
    )) }

    ParamedQuizTheme {
        Surface {
            MMQuizScreen(
                state = state.value,
                onEvent = { event ->
                    when(event){
                        is MMQuizUIEvents.OnAnswerClicked -> {
                            val updatedAnswers = state.value.answers.map { answer ->
                                if(answer.id == event.answerId) answer.copy(isSelected = !answer.isSelected)
                                else answer
                            }

                            state.value = state.value.copy(answers = updatedAnswers)
                        }
                        MMQuizUIEvents.OnBackDiscarded -> { state.value = state.value.copy(showExitConfirmation = false) }
                        MMQuizUIEvents.OnBackPressed -> { state.value = state.value.copy(showExitConfirmation = true) }
                        MMQuizUIEvents.OnNextQuestion -> { state.value = state.value.copy(
                            questionText = questionText,
                            answers = questionAnswers,
                            isAnswerSubmitted = false
                        ) }
                        MMQuizUIEvents.OnSubmitAnswer -> { state.value = state.value.copy(
                            isAnswerSubmitted = true,
                            isAnswerCorrect = state.value.answers.find { it.isSelected }?.id == 2L,
                            correctAnswers = questionAnswers.filter { it.id == 2L }.map { it.answerText }
                        ) }
                    }
                }
            ) { }
        }
    }
}

