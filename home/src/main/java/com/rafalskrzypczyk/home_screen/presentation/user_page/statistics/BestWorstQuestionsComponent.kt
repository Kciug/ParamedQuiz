package com.rafalskrzypczyk.home_screen.presentation.user_page.statistics

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.composables.ActionButton
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.Loading
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.core.ui.theme.MQRed
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.home.R
import com.rafalskrzypczyk.home_screen.domain.models.QuestionWithStats
import com.rafalskrzypczyk.home_screen.presentation.user_page.statistics.components.QuestionStatCard

@Composable
fun BestWorstQuestionsComponent(
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false,
    questions: BestWorstQuestionsUIM,
    onNextMode: () -> Unit,
    onPreviousMode: () -> Unit
) {
    val modeTitle = when(questions.currentMode) {
        QuizMode.MainMode -> stringResource(com.rafalskrzypczyk.core.R.string.title_main_mode)
        QuizMode.SwipeMode -> stringResource(com.rafalskrzypczyk.core.R.string.title_swipe_mode)
        QuizMode.TranslationMode -> stringResource(com.rafalskrzypczyk.core.R.string.title_translation_mode)
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
                    modifier = Modifier.fillMaxWidth(),
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
                NoBestWorstStatisticsComponent(modifier = if(isLandscape) Modifier else Modifier.aspectRatio(1f))
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
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL)
                ) {
                    DotIndicator(color = MQGreen)
                    TextPrimary(
                        text = stringResource(R.string.stats_best_questions),
                        textAlign = TextAlign.Start
                    )
                }
                
                bestQuestions.forEach { question ->
                    QuestionStatCard(question = question)
                }
                
                Row(
                    modifier = Modifier.padding(top = Dimens.ELEMENTS_SPACING),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL)
                ) {
                    DotIndicator(color = MQRed)
                    TextPrimary(
                        text = stringResource(R.string.stats_worst_questions),
                        textAlign = TextAlign.Start
                    )
                }
                
                worstQuestions.forEach { question ->
                    QuestionStatCard(question = question)
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
private fun DotIndicator(color: Color) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color)
    )
}
