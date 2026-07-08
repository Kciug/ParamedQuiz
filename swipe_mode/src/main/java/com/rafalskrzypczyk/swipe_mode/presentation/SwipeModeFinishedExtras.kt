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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
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
    isNewRecord: Boolean = false,
    averageResponseTimeMs: Long,
    totalDurationMs: Long,
    avgTimeCorrectMs: Long,
    avgTimeWrongMs: Long,
    fastestCorrectMs: Long,
    wrongAnswers: Int
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

                if (isNewRecord) {
                    NewRecordBadge()
                }
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

            // Speed & Accuracy Analysis
            SpeedAccuracySection(
                avgTimeCorrectMs = avgTimeCorrectMs,
                avgTimeWrongMs = avgTimeWrongMs,
                fastestCorrectMs = fastestCorrectMs,
                correctAnswers = correctAnswers,
                wrongAnswers = wrongAnswers
            )
        }
    }
}

@Composable
private fun NewRecordBadge() {
    Surface(
        shape = RoundedCornerShape(percent = 50),
        color = MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = Dimens.SMALL_PADDING,
                vertical = Dimens.ELEMENTS_SPACING_SMALL / 2
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL)
        ) {
            Icon(
                imageVector = Icons.Filled.EmojiEvents,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(16.dp)
            )
            TextCaption(
                text = stringResource(R.string.stats_new_combo_record),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun TimeStat(
    icon: ImageVector,
    value: String,
    label: String,
    tint: Color = MaterialTheme.colorScheme.secondary
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            TextPrimary(text = value)
            TextCaption(text = label)
        }
    }
}

private const val IMPULSIVE_RATIO = 0.7f
private const val HESITANT_RATIO = 1.3f
private const val MIN_ERRORS_FOR_VERDICT = 3

@Composable
fun SpeedAccuracySection(
    avgTimeCorrectMs: Long,
    avgTimeWrongMs: Long,
    fastestCorrectMs: Long,
    correctAnswers: Int,
    wrongAnswers: Int
) {
    val feedbackTitle: String
    val feedbackMsg: String
    val highlightColor: Color

    when {
        wrongAnswers == 0 && correctAnswers > 0 -> {
            feedbackTitle = stringResource(R.string.feedback_perfect_title)
            feedbackMsg = stringResource(R.string.feedback_perfect_msg)
            highlightColor = MQGreen
        }
        correctAnswers == 0 || wrongAnswers < MIN_ERRORS_FOR_VERDICT -> {
            feedbackTitle = stringResource(R.string.speed_verdict_insufficient_title)
            feedbackMsg = stringResource(R.string.speed_verdict_insufficient_msg)
            highlightColor = MaterialTheme.colorScheme.primary
        }
        avgTimeWrongMs < avgTimeCorrectMs * IMPULSIVE_RATIO -> {
            feedbackTitle = stringResource(R.string.speed_verdict_impulsive_title)
            feedbackMsg = stringResource(R.string.speed_verdict_impulsive_msg)
            highlightColor = MaterialTheme.colorScheme.tertiary
        }
        avgTimeWrongMs > avgTimeCorrectMs * HESITANT_RATIO -> {
            feedbackTitle = stringResource(R.string.speed_verdict_hesitant_title)
            feedbackMsg = stringResource(R.string.speed_verdict_hesitant_msg)
            highlightColor = MaterialTheme.colorScheme.error
        }
        else -> {
            feedbackTitle = stringResource(R.string.speed_verdict_balanced_title)
            feedbackMsg = stringResource(R.string.speed_verdict_balanced_msg)
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
                    imageVector = Icons.Rounded.Bolt,
                    contentDescription = null,
                    tint = highlightColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextHeadline(
                    text = stringResource(R.string.stats_speed_accuracy_title)
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

            if (avgTimeCorrectMs > 0 || avgTimeWrongMs > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TimeStat(
                        icon = Icons.Rounded.Check,
                        value = formatTime(avgTimeCorrectMs),
                        label = stringResource(R.string.stats_avg_time_correct),
                        tint = MQGreen
                    )
                    TimeStat(
                        icon = Icons.Rounded.Close,
                        value = formatTime(avgTimeWrongMs),
                        label = stringResource(R.string.stats_avg_time_wrong),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (fastestCorrectMs > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Bolt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    TextCaption(
                        text = stringResource(R.string.stats_fastest_correct, formatTime(fastestCorrectMs))
                    )
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
        "$minutes:${remainingSeconds.toString().padStart(2, '0')}"
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
            isNewRecord = true,
            averageResponseTimeMs = 1250,
            totalDurationMs = 45000,
            avgTimeCorrectMs = 1100,
            avgTimeWrongMs = 700,
            fastestCorrectMs = 480,
            wrongAnswers = 5
        )
    }
}
