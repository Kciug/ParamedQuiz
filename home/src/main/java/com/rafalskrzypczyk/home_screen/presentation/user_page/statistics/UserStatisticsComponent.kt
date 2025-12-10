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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.PreviewContainer
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.home.R
import com.rafalskrzypczyk.home_screen.domain.models.QuestionWithStats

@Composable
fun UserStatisticsComponent(
    modifier: Modifier = Modifier,
    overallResultAvailable: Boolean,
    mainModeResultAvailable: Boolean,
    swipeModeResultAvailable: Boolean,
    overallResult: Int,
    mainModeResult: Int,
    swipeModeResult: Int,
    bestWorstQuestions: BestWorstQuestionsUIM,
    onNextMode: () -> Unit,
    onPreviousMode: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = modifier.padding(Dimens.DEFAULT_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
    ) {
        TextHeadline(text = stringResource(R.string.stats_header))
        if(overallResultAvailable) {
            TextPrimary(text = stringResource(R.string.stats_overall))
            StatisticsChart(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(Dimens.DEFAULT_PADDING),
                isLandscape = isLandscape,
                progress = overallResult,
                numericalValueText = stringResource(R.string.percentage, overallResult),
                numericalValueDescription = stringResource(R.string.stats_correct_answers),
                strokeWidth = Dimens.STAT_BAR_WIDTH_THICK
            )
            HorizontalDivider()
            QuizModesResults(
                isLandscape = isLandscape,
                mainModeResultAvailable = mainModeResultAvailable,
                swipeModeResultAvailable = swipeModeResultAvailable,
                mainModeResult = mainModeResult,
                swipeModeResult = swipeModeResult
            )
            HorizontalDivider()
            BestWorstQuestionsComponent(
                isLandscape = isLandscape,
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
            overallResult = 85,
            mainModeResult = 73,
            swipeModeResult = 32,
            bestWorstQuestions = BestWorstQuestionsUIM(
                bestQuestions = listOf(
                    QuestionWithStats(
                        id = 1,
                        question = "Czy to wygląda dobrze?",
                        correctAnswers = 11,
                        wrongAnswers = 3
                    ),
                    QuestionWithStats(
                        id = 2,
                        question = "Czy to działa?",
                        correctAnswers = 7,
                        wrongAnswers = 4
                    )
                ),
                worstQuestions = listOf(
                    QuestionWithStats(
                        id = 1,
                        question = "Czy to wygląda źle?",
                        correctAnswers = 2,
                        wrongAnswers = 22
                    ),
                    QuestionWithStats(
                        id = 2,
                        question = "Czy to czasem przestało działac?",
                        correctAnswers = 7,
                        wrongAnswers = 4
                    )
                )
            ),
            onNextMode = {},
            onPreviousMode = {},
        )
    }
}