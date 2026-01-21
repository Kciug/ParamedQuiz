package com.rafalskrzypczyk.home_screen.presentation.user_page.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.home.R

@Composable
fun QuizModesResults(
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false,
    mainModeResultAvailable: Boolean,
    swipeModeResultAvailable: Boolean,
    translationModeResultAvailable: Boolean,
    mainModeResult: Int,
    swipeModeResult: Int,
    translationModeResult: Int
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuizModeResultElement(
                modifier = Modifier.weight(1f),
                isLandscape = isLandscape,
                title = stringResource(com.rafalskrzypczyk.core.R.string.title_main_mode),
                resultAvailable = mainModeResultAvailable,
                result = mainModeResult,
                resultDescription = stringResource(R.string.stats_correct_answers)
            )
            QuizModeResultElement(
                modifier = Modifier.weight(1f),
                isLandscape = isLandscape,
                title = stringResource(com.rafalskrzypczyk.core.R.string.title_swipe_mode),
                resultAvailable = swipeModeResultAvailable,
                result = swipeModeResult,
                resultDescription = stringResource(R.string.stats_correct_answers)
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(0.5f),
            horizontalArrangement = Arrangement.Center
        ) {
            QuizModeResultElement(
                modifier = Modifier.fillMaxWidth(),
                isLandscape = isLandscape,
                title = stringResource(R.string.stats_result_translation_mode),
                resultAvailable = translationModeResultAvailable,
                result = translationModeResult,
                resultDescription = stringResource(R.string.stats_correct_answers)
            )
        }
    }
}

@Composable
fun QuizModeResultElement(
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false,
    title: String,
    resultAvailable: Boolean,
    result: Int,
    resultDescription: String
) {
    Column(
        modifier = modifier.padding(Dimens.DEFAULT_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
    ) {
        if(resultAvailable) {
            TextPrimary(text = title)
            StatisticsChart(
                isLandscape = isLandscape,
                progress = result,
                numericalValueText = stringResource(R.string.percentage, result),
                numericalValueDescription = resultDescription,
            )
        } else {
            NoStatisticsForModeComponent(modeTitle = title)
        }
    }
}

@Composable
fun StatisticsChart(
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false,
    progress: Int,
    numericalValueText: String,
    numericalValueDescription: String,
    strokeWidth: Dp = Dimens.STAT_BAR_WIDTH
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxWidth(if (isLandscape) 0.5f else 1f)
                .aspectRatio(1f),
            progress = { progress / 100f },
            strokeWidth = strokeWidth,
            strokeCap = StrokeCap.Round,
            gapSize = -SliderDefaults.TrackStopIndicatorSize * strokeWidth.value
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextHeadline(text = numericalValueText)
            Spacer(Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
            TextCaption(
                modifier = Modifier.fillMaxWidth(0.5f),
                text = numericalValueDescription,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}