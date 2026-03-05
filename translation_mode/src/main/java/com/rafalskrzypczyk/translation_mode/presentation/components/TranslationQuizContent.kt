package com.rafalskrzypczyk.translation_mode.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rafalskrzypczyk.core.composables.ActionButton
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.composables.TextTitle
import com.rafalskrzypczyk.translation_mode.R
import com.rafalskrzypczyk.translation_mode.presentation.TranslationQuizEvents
import com.rafalskrzypczyk.translation_mode.presentation.TranslationQuizState

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
                        TextTitle(
                            text = question.phrase,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            autoSize = TextAutoSize.StepBased(
                                minFontSize = 14.sp,
                                maxFontSize = 32.sp,
                                stepSize = 1.sp
                            )
                        )
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
                    readOnly = false,
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
