package com.rafalskrzypczyk.home_screen.presentation.home_screen.user_page

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.home.R

@Composable
fun QuizModesResults(
    modifier: Modifier = Modifier,
    mainModeResult: Int,
    swipeModeResult: Int
) {
    Row(modifier = modifier) {
        QuizModeResultElement(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.stats_result_main_mode),
            result = mainModeResult,
            resultDescription = stringResource(R.string.stats_correct_answers)
        )
        QuizModeResultElement(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.stats_result_swipe_mode),
            result = swipeModeResult,
            resultDescription = stringResource(R.string.stats_correct_answers)
        )
    }
}

@Composable
fun QuizModeResultElement(
    modifier: Modifier = Modifier,
    title: String,
    result: Int,
    resultDescription: String
) {
    Column(
        modifier = modifier.padding(Dimens.DEFAULT_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
    ) {
        TextPrimary(text = title)
        StatisticsChart(
            progress = result,
            numericalValueText = stringResource(R.string.percentage, result),
            numericalValueDescription = resultDescription,
        )
    }
}

@Composable
fun StatisticsChart(
    modifier: Modifier = Modifier,
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
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            progress = { progress / 100f },
            strokeWidth = strokeWidth
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextHeadline(text = numericalValueText)
            Spacer(Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
            TextCaption(
                modifier = Modifier.fillMaxWidth(0.5f),
                text = numericalValueDescription,
                textAlign = TextAlign.Center
            )
        }
    }
}