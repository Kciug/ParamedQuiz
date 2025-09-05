package com.rafalskrzypczyk.home_screen.presentation.home_screen.user_page

import android.content.res.Configuration
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.PreviewContainer
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.home.R

@Composable
fun UserStatisticsComponent(
    modifier: Modifier = Modifier,
    overallResult: Float,
    mainModeResult: Float,
    swipeModeResult: Float
) {
    Column(
        modifier = modifier.padding(Dimens.DEFAULT_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
    ) {
        TextHeadline(text = stringResource(R.string.stats_header))
        TextPrimary(text = stringResource(R.string.stats_overall))
        StatisticsChart(
            modifier = Modifier.fillMaxWidth(),
            progress = overallResult,
            numericalValueText = "${(overallResult * 100).toInt()}%",
            numericalValueDescription = stringResource(R.string.stats_correct_answers),
            strokeWidth = Dimens.STAT_BAR_WIDTH_THICK
        )
        QuizModesResults(
            mainModeResult = mainModeResult,
            swipeModeResult = swipeModeResult
        )
    }
}

@Composable
fun QuizModesResults(
    modifier: Modifier = Modifier,
    mainModeResult: Float,
    swipeModeResult: Float
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
    result: Float,
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
            numericalValueText = "${(result * 100).toInt()}%",
            numericalValueDescription = resultDescription,
        )
    }
}

@Composable
fun StatisticsChart(
    modifier: Modifier = Modifier,
    progress: Float,
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
            progress = { progress },
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



@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun UserStatisticsComponentPreview() {
    PreviewContainer {
        UserStatisticsComponent(
            overallResult = 0.8f,
            mainModeResult = 0.7f,
            swipeModeResult = 0.3f
        )
    }
}