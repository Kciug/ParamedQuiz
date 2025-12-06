package com.rafalskrzypczyk.main_mode.presentation.quiz_base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.PrecisionManufacturing
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.PreviewContainer
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.main_mode.R
import java.util.concurrent.TimeUnit

@Composable
fun MMQuizFinishedExtras(
    modifier: Modifier = Modifier,
    correctAnswers: Int,
    totalQuestions: Int,
    averageResponseTimeMs: Long,
    totalDurationMs: Long,
    averagePrecision: Int,
    onReviewAnswersClick: () -> Unit
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
            TextHeadline(
                text = stringResource(com.rafalskrzypczyk.core.R.string.title_main_mode),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Accuracy Chart
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
            ) {
                Box(contentAlignment = Alignment.Center) {
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
                TextPrimary(text = stringResource(R.string.stats_accuracy))
            }

            // Precision
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
            ) {
                Icon(
                    imageVector = Icons.Rounded.PrecisionManufacturing,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    TextHeadline(
                        text = "$averagePrecision%",
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextPrimary(text = stringResource(R.string.stats_precision))
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

            Button(
                onClick = onReviewAnswersClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Dimens.RADIUS_SMALL)
            ) {
                Text(text = stringResource(R.string.btn_review_answers))
            }
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
fun QuestionReviewDialog(
    questions: List<QuestionUIM>,
    onDismiss: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.DEFAULT_PADDING)
                .heightIn(max = screenHeight * 0.85f),
            shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(Dimens.DEFAULT_PADDING)
            ) {
                TextHeadline(
                    text = stringResource(R.string.review_answers_title),
                    modifier = Modifier.padding(bottom = Dimens.ELEMENTS_SPACING)
                )
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
                ) {
                    items(questions) { question ->
                        QuestionReviewItem(question)
                    }
                }
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.ELEMENTS_SPACING)
                ) {
                    Text(text = stringResource(com.rafalskrzypczyk.core.R.string.btn_confirm_OK))
                }
            }
        }
    }
}

@Composable
fun QuestionReviewItem(question: QuestionUIM) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(Dimens.RADIUS_SMALL)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.DEFAULT_PADDING),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextPrimary(text = question.questionText, maxLines = Int.MAX_VALUE)
            
            TextCaption(
                text = stringResource(R.string.question_precision, question.userPrecision),
                color = if(question.userPrecision == 100) MQGreen else MaterialTheme.colorScheme.error
            )

            HorizontalDivider()

            question.answers.forEach { answer ->
                val isSelected = answer.isSelected
                val isCorrect = question.correctAnswerIds.contains(answer.id)
                
                val backgroundColor = when {
                    isCorrect -> MQGreen.copy(alpha = 0.2f)
                    isSelected && !isCorrect -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                    else -> Color.Transparent
                }
                
                val statusIcon = when {
                    isCorrect -> Icons.Outlined.Check
                    isSelected && !isCorrect -> Icons.Outlined.Close
                    else -> null
                }
                
                val contentColor = when {
                    isCorrect -> MQGreen
                    isSelected && !isCorrect -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor, RoundedCornerShape(Dimens.RADIUS_SMALL)) // Changed to Dimens.RADIUS_SMALL
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (statusIcon != null) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    
                    Text(
                        text = answer.answerText,
                        color = contentColor,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (isSelected) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Rounded.RadioButtonChecked,
                            contentDescription = "Selected",
                            tint = contentColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
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
private fun MMQuizFinishedExtrasPreview() {
    PreviewContainer {
        MMQuizFinishedExtras(
            correctAnswers = 8,
            totalQuestions = 10,
            averageResponseTimeMs = 5000,
            totalDurationMs = 50000,
            averagePrecision = 75,
            onReviewAnswersClick = {}
        )
    }
}
