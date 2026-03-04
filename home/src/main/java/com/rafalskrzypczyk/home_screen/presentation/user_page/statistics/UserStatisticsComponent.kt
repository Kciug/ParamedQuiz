package com.rafalskrzypczyk.home_screen.presentation.user_page.statistics

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.PreviewContainer
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.home.R
import com.rafalskrzypczyk.home_screen.domain.models.QuestionWithStats
import com.rafalskrzypczyk.home_screen.presentation.user_page.statistics.components.ModeScoreTile
import com.rafalskrzypczyk.home_screen.presentation.user_page.statistics.components.OverallScoreTile

@Composable
fun UserStatisticsComponent(
    modifier: Modifier = Modifier,
    overallResultAvailable: Boolean,
    mainModeResultAvailable: Boolean,
    swipeModeResultAvailable: Boolean,
    translationModeResultAvailable: Boolean,
    overallResult: Int,
    mainModeResult: Int,
    swipeModeResult: Int,
    translationModeResult: Int,
    bestWorstQuestions: BestWorstQuestionsUIM,
    onNextMode: () -> Unit,
    onPreviousMode: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
    ) {
        if(overallResultAvailable) {
            OverallScoreTile(score = overallResult)
            
            HorizontalDivider()
            
            TextPrimary(text = stringResource(R.string.stats_modes_results))
            
            ModeScoreTile(
                title = stringResource(com.rafalskrzypczyk.core.R.string.title_main_mode),
                score = mainModeResult,
                isAvailable = mainModeResultAvailable
            )
            
            ModeScoreTile(
                title = stringResource(com.rafalskrzypczyk.core.R.string.title_swipe_mode),
                score = swipeModeResult,
                isAvailable = swipeModeResultAvailable
            )
            
            ModeScoreTile(
                title = stringResource(R.string.stats_result_translation_mode),
                score = translationModeResult,
                isAvailable = translationModeResultAvailable
            )
            
            HorizontalDivider()
            
            BestWorstQuestionsComponent(
                isLandscape = false,
                questions = bestWorstQuestions,
                onNextMode = onNextMode,
                onPreviousMode = onPreviousMode
            )
        } else {
            NoStatisticsDataComponent(modifier = Modifier.padding(top = Dimens.DEFAULT_PADDING))
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun UserStatisticsComponentPreview() {
    PreviewContainer {
        UserStatisticsComponent(
            overallResultAvailable = true,
            mainModeResultAvailable = true,
            swipeModeResultAvailable = true,
            translationModeResultAvailable = true,
            overallResult = 85,
            mainModeResult = 73,
            swipeModeResult = 32,
            translationModeResult = 55,
            bestWorstQuestions = BestWorstQuestionsUIM(
                bestQuestions = listOf(
                    QuestionWithStats(
                        id = 1,
                        question = "Czy to wygląda dobrze?",
                        correctAnswers = 11,
                        wrongAnswers = 3
                    )
                ),
                worstQuestions = listOf(
                    QuestionWithStats(
                        id = 1,
                        question = "Czy to wygląda źle?",
                        correctAnswers = 2,
                        wrongAnswers = 22
                    )
                )
            ),
            onNextMode = {},
            onPreviousMode = {},
        )
    }
}
