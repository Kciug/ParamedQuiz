package com.rafalskrzypczyk.swipe_mode.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.PreviewContainer
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.swipe_mode.R

@Composable
fun SwipeModeFinishedExtras(
    modifier: Modifier = Modifier,
    correctAnswers: Int,
    totalQuestions: Int,
    bestStreak: Int
) {
    val progress = if (totalQuestions > 0) correctAnswers.toFloat() / totalQuestions else 0f
    val percentage = (progress * 100).toInt()

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = Dimens.ELEVATION
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.LARGE_PADDING),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
        ) {
            // Title
            TextHeadline(
                text = stringResource(com.rafalskrzypczyk.core.R.string.title_swipe_mode),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))

            // Chart Section (Effectiveness)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING, Alignment.CenterHorizontally)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(60.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        strokeWidth = 6.dp
                    )
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(60.dp),
                        strokeCap = StrokeCap.Round,
                        strokeWidth = 6.dp,
                        color = MQGreen
                    )
                    TextPrimary(text = "$percentage%")
                }
                TextPrimary(text = stringResource(R.string.swipe_mode_effectiveness))
            }

            // Streak Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
            ) {
                TextHeadline(
                    text = bestStreak.toString(),
                    color = MaterialTheme.colorScheme.primary
                )
                TextPrimary(text = stringResource(R.string.swipe_mode_best_streak))
            }
        }
    }
}

@Preview
@Composable
private fun SwipeModeFinishedExtrasPreview() {
    PreviewContainer {
        SwipeModeFinishedExtras(
            correctAnswers = 15,
            totalQuestions = 20,
            bestStreak = 8
        )
    }
}
