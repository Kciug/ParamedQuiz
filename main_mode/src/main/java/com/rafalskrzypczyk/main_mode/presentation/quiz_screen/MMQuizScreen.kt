package com.rafalskrzypczyk.main_mode.presentation.quiz_screen

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.ErrorDialog
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.QuizLinearProgressBar
import com.rafalskrzypczyk.core.ui.NavigationTopBar

@Composable
fun MMQuizScreen(
    state: MMQuizState,
    onEvent: (MMQuizUIEvents) -> Unit,
    onNavigateBack: () -> Unit,
) {
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
                Column (
                    modifier = modifier
                        .fillMaxSize()
                        .padding(horizontal = Dimens.DEFAULT_PADDING)
                        .padding(bottom = Dimens.DEFAULT_PADDING),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    QuizQuestionSection(
                        questionNumber = state.currentQuestionNumber,
                        questionsCount = state.questionsCount,
                        questionText = state.questionText
                    )
                    AnswersSection(
                        answers = state.answers,
                        onAnswerSelected = { answer -> onEvent.invoke(MMQuizUIEvents.OnAnswerClicked(answer))},
                        isSubmitted = state.isAnswerSubmitted,
                        onSubmit = { onEvent.invoke(MMQuizUIEvents.OnSubmitAnswer) },
                        onNextQuestion = { onEvent.invoke(MMQuizUIEvents.OnNextQuestion) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuizQuestionSection(
    questionNumber: Int,
    questionsCount: Int,
    questionText: String,
) {
    Column (horizontalAlignment = Alignment.CenterHorizontally) {
        QuizLinearProgressBar(progress = questionNumber, range = questionsCount)
        Spacer(modifier = Modifier.height(Dimens.DEFAULT_PADDING))
        Text(
            text = questionText,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AnswersSection(
    answers: List<AnswerUIM>,
    onAnswerSelected: (Long) -> Unit,
    isSubmitted: Boolean,
    onSubmit: () -> Unit,
    onNextQuestion: () -> Unit
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
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
        ButtonPrimary(
            title = if(isSubmitted) "Następne pytanie" else "Odpowiedz",
            onClick = if(isSubmitted) onNextQuestion else onSubmit
        )
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

