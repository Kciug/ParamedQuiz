package com.rafalskrzypczyk.main_mode.presentation.quiz_base

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
    titlePanel: @Composable () -> Unit = {},
    scaffoldPadding: PaddingValues,
    question: QuestionUIM,
    onAnswerSelected: (Long) -> Unit,
    onSubmitAnswer: () -> Unit,
    onNextQuestion: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        bottom = scaffoldPadding.calculateBottomPadding(),
                        start = Dimens.DEFAULT_PADDING,
                        end = Dimens.DEFAULT_PADDING
                    ),
                horizontalArrangement = Arrangement.spacedBy(Dimens.DEFAULT_PADDING)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    titlePanel.invoke()
                    QuizQuestionTextElement(
                        modifier = Modifier.weight(1f),
                        text = question.questionText,
                        overflow = TextOverflow.Visible
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    AnswersListSection(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        answers = question.answers,
                        onAnswerSelected = onAnswerSelected,
                        isSubmitted = question.isAnswerSubmitted
                    )
                    ButtonPrimary(
                        modifier = Modifier.padding(top = Dimens.DEFAULT_PADDING),
                        title = stringResource(R.string.btn_submit),
                        onClick = onSubmitAnswer
                    )
                }
            }
        } else {
            val scrollState = rememberScrollState()
            val isScrolled by remember { derivedStateOf { scrollState.value > 0 } }
            val canScroll by remember { derivedStateOf { scrollState.maxValue > 0 } }

            Column(modifier = Modifier.fillMaxSize()) {
                if (isScrolled && canScroll) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }

                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val maxHeight = this.maxHeight
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(
                                state = scrollState,
                                enabled = canScroll
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = maxHeight - scaffoldPadding.calculateBottomPadding())
                                .padding(horizontal = Dimens.DEFAULT_PADDING),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.weight(1f))

                            QuizQuestionTextElement(
                                text = question.questionText,
                                overflow = TextOverflow.Visible
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Spacer(modifier = Modifier.height(Dimens.DEFAULT_PADDING))

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AnswersListSection(
                                    answers = question.answers,
                                    onAnswerSelected = onAnswerSelected,
                                    isSubmitted = question.isAnswerSubmitted
                                )

                                ButtonPrimary(
                                    modifier = Modifier.padding(
                                        top = Dimens.DEFAULT_PADDING,
                                        bottom = Dimens.DEFAULT_PADDING
                                    ),
                                    title = stringResource(R.string.btn_submit),
                                    onClick = onSubmitAnswer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(scaffoldPadding.calculateBottomPadding()))
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = question.isAnswerSubmitted,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            QuizSubmittedSection(
                bottomPadding = scaffoldPadding.calculateBottomPadding(),
                isAnswerCorrect = question.isAnswerCorrect,
                correctAnswers = question.correctAnswers,
                onNextQuestion = onNextQuestion
            )
        }
    }
}


@Composable
fun QuizQuestionTextElement(
    modifier: Modifier = Modifier,
    text: String,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        TextPrimary(
            text = text,
            textAlign = TextAlign.Center,
            overflow = overflow
        )
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
        modifier = modifier,
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.BUTTON_PADDING, horizontal = Dimens.DEFAULT_PADDING),
            contentAlignment = Alignment.Center
        ) {
            TextPrimary(
                text = answer.answerText,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun QuizGameContentPreview() {
    var answers by remember {
        mutableStateOf(
            listOf(
                AnswerUIM(1, "Odpowiedz", false),
                AnswerUIM(2, "Dluzsza odpowiedz", false),
                AnswerUIM(3, "Jeszcze troche dluzsza odpowiedz", false),
                AnswerUIM(4, "Taka odpowiedz moze sie juz nie miescic w tym okienku", false)
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
            onSubmitAnswer = {
                submitted = true
            },
            onNextQuestion = {},
            scaffoldPadding = PaddingValues(0.dp)
        )
    }
}