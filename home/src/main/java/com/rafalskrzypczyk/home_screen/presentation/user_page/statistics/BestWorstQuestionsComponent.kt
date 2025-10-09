package com.rafalskrzypczyk.home_screen.presentation.user_page.statistics

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.outlined.Info
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.ActionButton
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.PreviewContainer
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.home.R
import com.rafalskrzypczyk.home_screen.domain.models.QuestionWithStats
import com.rafalskrzypczyk.home_screen.domain.models.QuizMode

@Composable
fun BestWorstQuestionsComponent(
    modifier: Modifier = Modifier,
    questions: BestWorstQuestionsUIM,
    onNextMode: () -> Unit,
    onPreviousMode: () -> Unit
) {
    val modeTitle = when(questions.currentMode) {
        QuizMode.MainMode -> stringResource(com.rafalskrzypczyk.core.R.string.title_main_mode)
        QuizMode.SwipeMode -> stringResource(com.rafalskrzypczyk.core.R.string.title_swipe_mode)
    }

    var movedToNext by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(
                icon = Icons.AutoMirrored.Default.ArrowLeft,
                description = stringResource(R.string.desc_previous_mode),
                onClick = {
                    movedToNext = false
                    onPreviousMode()
                }
            )
            AnimatedContent(
                targetState = modeTitle,
                modifier = Modifier.weight(1f),
                label = "currentMode",
                transitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { if(movedToNext) it else -it }
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { if(movedToNext) -it else it }
                    )
                }
            ) {
                TextPrimary(
                    text = it,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
            ActionButton(
                icon = Icons.AutoMirrored.Default.ArrowRight,
                description = stringResource(R.string.desc_next_mode),
                onClick = {
                    movedToNext = true
                    onNextMode()
                }
            )
        }
        AnimatedContent(
            targetState = questions,
            label = "currentMode",
            contentKey = { data -> data.currentMode },
            transitionSpec = {
                slideInHorizontally(
                    initialOffsetX = { if(movedToNext) it else -it }
                ) togetherWith slideOutHorizontally(
                    targetOffsetX = { if(movedToNext) -it else it }
                )
            }
        ) { questionsData ->
            if (questionsData.dataAvailable){
                BestWorstQuestionsList(
                    responseState = questionsData.responseState,
                    bestQuestions = questionsData.bestQuestions,
                    worstQuestions = questionsData.worstQuestions
                )
            } else {
                NoBestWorstStatisticsComponent()
            }
        }
    }
}

@Composable
fun BestWorstQuestionsList(
    modifier: Modifier = Modifier,
    responseState: ResponseState,
    bestQuestions: List<QuestionWithStats>,
    worstQuestions: List<QuestionWithStats>
) {
    AnimatedContent(
        targetState = responseState,
        label = "responseState",
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
    ) { state ->
        if (state is ResponseState.Success) {
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
            ) {
                TextPrimary(
                    text = stringResource(R.string.stats_best_questions),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                bestQuestions.forEach {
                    QuestionStatCard(question = it)
                }
                TextPrimary(
                    text = stringResource(R.string.stats_worst_questions),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                worstQuestions.forEach {
                    QuestionStatCard(question = it)
                }
            }
        } else {
            Loading(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
        }
    }
}

@Composable
fun QuestionStatCard(
    modifier: Modifier = Modifier,
    question: QuestionWithStats,
    collapsedMaxLines: Int = 3
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card (
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = { isExpanded = !isExpanded },
    ) {
        Column(
            modifier = Modifier.padding(Dimens.DEFAULT_PADDING)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
            ) {
                TextPrimary(
                    text = question.question,
                    modifier = Modifier.weight(1f),
                    maxLines = if(isExpanded) Int.MAX_VALUE else collapsedMaxLines
                )
                TextPrimary( stringResource(R.string.percentage, question.correctPercentage) )
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = stringResource(R.string.desc_expand)
                )
            }
//            if(isExpanded){
//                TextCaption(
//                    text = stringResource(R.string.stats_question_correct_answers, question.correctAnswers)
//                )
//                TextCaption(
//                    text = stringResource(R.string.stats_question_incorrect_answers, question.wrongAnswers)
//                )
//            }
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Column(
                    modifier = Modifier.padding(top = Dimens.ELEMENTS_SPACING),
                    verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
                ) {
                    TextCaption(
                        text = stringResource(R.string.stats_question_correct_answers, question.correctAnswers)
                    )
                    TextCaption(
                        text = stringResource(R.string.stats_question_incorrect_answers, question.wrongAnswers)
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun QuestionStatCardPreview() {
    PreviewContainer {
        QuestionStatCard(
            question = QuestionWithStats(
                id = 1,
                question = "Czy to wygląda dobrze?",
                correctAnswers = 11,
                wrongAnswers = 25,
            )
        )
    }
}