package com.rafalskrzypczyk.home_screen.presentation.user_page.statistics

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.PreviewContainer
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.utils.QuizMode
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
    cemModeResultAvailable: Boolean,
    overallResult: Int,
    mainModeResult: Int,
    mainModeCorrect: Int = 0,
    mainModeTotal: Int = 0,
    swipeModeResult: Int,
    swipeModeCorrect: Int = 0,
    swipeModeTotal: Int = 0,
    translationModeResult: Int,
    translationModeCorrect: Int = 0,
    translationModeTotal: Int = 0,
    cemModeResult: Int,
    cemModeCorrect: Int = 0,
    cemModeTotal: Int = 0,
    totalCorrect: Int,
    totalIncorrect: Int,
    totalUnique: Int,
    totalIdeal: Int,
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
            OverallScoreTile(
                score = overallResult,
                totalCorrect = totalCorrect,
                totalIncorrect = totalIncorrect,
                totalUnique = totalUnique,
                totalIdeal = totalIdeal
            )
            
            TextHeadline(text = stringResource(R.string.stats_modes_results))
            
            ModeScoreTile(
                mode = QuizMode.MainMode,
                title = stringResource(com.rafalskrzypczyk.core.R.string.title_main_mode),
                score = mainModeResult,
                correctAnswers = mainModeCorrect,
                totalAnswers = mainModeTotal,
                isAvailable = mainModeResultAvailable
            )
            
            ModeScoreTile(
                mode = QuizMode.SwipeMode,
                title = stringResource(com.rafalskrzypczyk.core.R.string.title_swipe_mode),
                score = swipeModeResult,
                correctAnswers = swipeModeCorrect,
                totalAnswers = swipeModeTotal,
                isAvailable = swipeModeResultAvailable
            )
            
            ModeScoreTile(
                mode = QuizMode.TranslationMode,
                title = stringResource(R.string.stats_result_translation_mode),
                score = translationModeResult,
                correctAnswers = translationModeCorrect,
                totalAnswers = translationModeTotal,
                isAvailable = translationModeResultAvailable
            )

            ModeScoreTile(
                mode = QuizMode.CemMode,
                title = stringResource(R.string.stats_result_cem_mode),
                score = cemModeResult,
                correctAnswers = cemModeCorrect,
                totalAnswers = cemModeTotal,
                isAvailable = cemModeResultAvailable
            )
            
            TextHeadline(text = stringResource(R.string.stats_best_worst_header))
            
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
            mainModeCorrect = 73,
            mainModeTotal = 100,
            swipeModeResult = 32,
            swipeModeCorrect = 32,
            swipeModeTotal = 100,
            translationModeResult = 55,
            translationModeCorrect = 55,
            translationModeTotal = 100,
            cemModeResultAvailable = true,
            cemModeResult = 45,
            cemModeCorrect = 45,
            cemModeTotal = 100,
            totalCorrect = 120,
            totalIncorrect = 20,
            totalUnique = 100,
            totalIdeal = 50,
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
