package com.rafalskrzypczyk.main_mode.presentation.quiz_base

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.PreviewContainer
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.main_mode.R

@Composable
fun QuizGameContent(
    modifier: Modifier = Modifier,
    question: QuestionUIM,
    onAnswerSelected: (Long) -> Unit,
    onSubmitAnswer: () -> Unit,
    onNextQuestion: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.DEFAULT_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                TextPrimary(
                    text = question.questionText,
                    textAlign = TextAlign.Center
                )
            }
            AnswersListSection(
                answers = question.answers,
                onAnswerSelected = onAnswerSelected,
                isSubmitted = question.isAnswerSubmitted
            )
            ButtonPrimary(
                modifier = Modifier.padding(top = Dimens.DEFAULT_PADDING),
                title = stringResource(R.string.btn_submit),
                onClick = onSubmitAnswer,
            )
        }
        AnimatedVisibility(
            visible = question.isAnswerSubmitted,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = modifier.align(Alignment.BottomCenter),
        ) {
            SubmitSection(
                isAnswerCorrect = question.isAnswerCorrect,
                correctAnswers = question.correctAnswers,
                onNextQuestion = onNextQuestion
            )
        }
    }
}

@Composable
fun AnswersListSection(
    modifier: Modifier = Modifier,
    answers: List<AnswerUIM>,
    onAnswerSelected: (Long) -> Unit,
    isSubmitted: Boolean,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        answers.forEach {
            AnswerButton(
                answer = it,
                isSubmitted = isSubmitted,
                onSelected = onAnswerSelected
            )
        }
    }
}

@Composable
fun AnswerButton(
    modifier: Modifier = Modifier,
    answer: AnswerUIM,
    isSubmitted: Boolean,
    onSelected: (Long) -> Unit
) {
    Card(
        modifier = modifier.wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
        border = if (answer.isSelected) {
            BorderStroke(Dimens.OUTLINE_THICKNESS, MaterialTheme.colorScheme.primary)
        } else {
            null
        },
        enabled = isSubmitted.not(),
        onClick = { onSelected(answer.id) },
    ) {
        TextPrimary(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            text = answer.answerText,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SubmitSection(
    isAnswerCorrect: Boolean,
    correctAnswers: List<String>,
    onNextQuestion: () -> Unit
) {
    val verifiedIcon = if(isAnswerCorrect) Icons.Default.Check else Icons.Default.Close
    val verifiedText = stringResource(if(isAnswerCorrect) R.string.answer_correct else R.string.answer_incorrect)
    val verifiedColor = if(isAnswerCorrect) MaterialTheme.colorScheme.tertiary
        else MaterialTheme.colorScheme.secondary

    Column(modifier = Modifier
        .fillMaxWidth()
        .clip(
            shape = RoundedCornerShape(
                topStart = Dimens.RADIUS_SMALL,
                topEnd = Dimens.RADIUS_SMALL
            )
        )
        .background(MaterialTheme.colorScheme.surfaceContainer)
        .padding(Dimens.DEFAULT_PADDING)
    ) {
        Row {
            Icon(
                imageVector = verifiedIcon,
                contentDescription = stringResource(R.string.ic_desc_answer_correctness),
                tint = verifiedColor
            )
            Spacer(modifier = Modifier.width(Dimens.ELEMENTS_SPACING_SMALL))
            TextPrimary(
                text = verifiedText,
                color = verifiedColor,
            )
        }

        if (!isAnswerCorrect) {
            Spacer(modifier = Modifier.height(Dimens.DEFAULT_PADDING))
            TextPrimary(
                text = stringResource(
                    if(correctAnswers.count() > 1) R.string.question_correct_answers_plural
                    else R.string.question_correct_answers_single
                )
            )
            correctAnswers.forEach {
                CorrectAnswerElement(text = it)
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
fun CorrectAnswerElement(
    text: String
) {
    TextPrimary(
        modifier = Modifier
            .padding(Dimens.SMALL_PADDING)
            .clip(shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = Dimens.SMALL_PADDING)
            .padding(horizontal = Dimens.DEFAULT_PADDING),
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}



@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun QuizGameContentPreview() {
    var answers by remember {
        mutableStateOf(
            listOf(
                AnswerUIM(1, "A", false),
                AnswerUIM(2, "B", false),
                AnswerUIM(3, "C", false),
                AnswerUIM(4, "D", false)
            )
        )
    }
    var submitted by remember { mutableStateOf(false) }

    PreviewContainer {
        QuizGameContent(
            question = QuestionUIM(
                questionText = "Przykladowe pytanie",
                answers = answers,
                isAnswerSubmitted = submitted,
                isAnswerCorrect = false,
                correctAnswers = listOf("A", "B")
            ),
            onAnswerSelected = { id ->
                answers = answers.map { answer ->
                    if (answer.id == id) {
                        answer.copy(isSelected = !answer.isSelected) // toggle boola
                    } else {
                        answer
                    }
                }
            },
            onSubmitAnswer = { submitted = true },
            onNextQuestion = {}
        )
    }
}