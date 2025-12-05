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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.PreviewContainer
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.swipe_mode.R
import java.util.concurrent.TimeUnit

@Composable
fun SwipeModeFinishedExtras(
    modifier: Modifier = Modifier,
    correctAnswers: Int,
    totalQuestions: Int,
    bestStreak: Int,
    averageResponseTimeMs: Long,
    totalDurationMs: Long,
    type1Errors: Int,
    type2Errors: Int
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
                .padding(Dimens.DEFAULT_PADDING),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
        ) {
            // Title
            TextHeadline(
                text = stringResource(com.rafalskrzypczyk.core.R.string.title_swipe_mode),
                color = MaterialTheme.colorScheme.onSurface
            )

            // USUNIĘTO SPACER - odstęp reguluje teraz Arrangement.spacedBy rodzica (Column)

            // Chart Section (Effectiveness)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
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
                    TextPrimary(
                        text = "$percentage%"
                    )
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
                TextPrimary(text = stringResource(R.string.stats_best_streak))
            }

            // Time Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TimeStat(
                    icon = Icons.Rounded.AccessTime,
                    value = formatTime(averageResponseTimeMs),
                    label = stringResource(R.string.stats_avg_time)
                )
                TimeStat(
                    icon = Icons.Outlined.Timer,
                    value = formatTime(totalDurationMs),
                    label = stringResource(R.string.stats_total_time)
                )
            }

            // Error Analysis
            ErrorAnalysisSection(
                type1Errors = type1Errors,
                type2Errors = type2Errors
            )
        }
    }
}

@Composable
fun TimeStat(
    icon: ImageVector,
    value: String,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            TextPrimary(text = value)
            TextCaption(text = label)
        }
    }
}

@Composable
fun ErrorAnalysisSection(
    type1Errors: Int,
    type2Errors: Int
) {
    val totalErrors = type1Errors + type2Errors
    
    val feedbackTitle: String
    val feedbackMsg: String
    val highlightColor: Color

    when {
        totalErrors == 0 -> {
            feedbackTitle = stringResource(R.string.feedback_perfect_title)
            feedbackMsg = stringResource(R.string.feedback_perfect_msg)
            highlightColor = MQGreen
        }
        type1Errors > type2Errors -> {
            feedbackTitle = stringResource(R.string.feedback_type_1_title)
            feedbackMsg = stringResource(R.string.feedback_type_1_msg)
            highlightColor = MaterialTheme.colorScheme.tertiary
        }
        type2Errors > type1Errors -> {
            feedbackTitle = stringResource(R.string.feedback_type_2_title)
            feedbackMsg = stringResource(R.string.feedback_type_2_msg)
            highlightColor = MaterialTheme.colorScheme.error
        }
        else -> {
            feedbackTitle = stringResource(R.string.feedback_balanced_title)
            feedbackMsg = stringResource(R.string.feedback_balanced_msg)
            highlightColor = MaterialTheme.colorScheme.primary
        }
    }

    Card(
        shape = RoundedCornerShape(Dimens.RADIUS_INNER_DEFAULT),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(Dimens.DEFAULT_PADDING),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = highlightColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextHeadline(
                    text = stringResource(R.string.stats_error_analysis_title)
                )
            }
            
            TextPrimary(
                text = feedbackTitle,
                color = highlightColor
            )
            
            TextPrimary(
                text = feedbackMsg,
                maxLines = Int.MAX_VALUE
            )

            if (totalErrors > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TextCaption(text = "${stringResource(R.string.stats_type_1_error)}: $type1Errors")
                    TextCaption(text = "${stringResource(R.string.stats_type_2_error)}: $type2Errors")
                }
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
    return if (seconds < 60) {
        "${seconds}s"
    } else {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        val remainingSeconds = seconds - TimeUnit.MINUTES.toSeconds(minutes)
        String.format("%d:%02d", minutes, remainingSeconds)
    }
}

@Preview
@Composable
private fun SwipeModeFinishedExtrasPreview() {
    PreviewContainer {
        SwipeModeFinishedExtras(
            correctAnswers = 15,
            totalQuestions = 20,
            bestStreak = 8,
            averageResponseTimeMs = 1250,
            totalDurationMs = 45000,
            type1Errors = 5,
            type2Errors = 2
        )
    }
}
