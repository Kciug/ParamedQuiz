package com.rafalskrzypczyk.translation_mode.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.core.ui.theme.MQRed
import com.rafalskrzypczyk.translation_mode.R
import com.rafalskrzypczyk.translation_mode.domain.TranslationQuestionUIM

@Composable
fun TranslationQuizFinishedExtras(
    questions: List<TranslationQuestionUIM>,
    onReviewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val answeredQuestions = questions.count { it.isAnswered }
    val correctAnswers = questions.count { it.isCorrect }
    val percentage = if (answeredQuestions > 0) ((correctAnswers.toFloat() / answeredQuestions) * 100).toInt() else 0

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
                text = stringResource(com.rafalskrzypczyk.core.R.string.title_translation_mode),
                color = MaterialTheme.colorScheme.onSurface
            )

            TextPrimary(
                text = stringResource(R.string.extras_accuracy, percentage),
                color = if (percentage >= 50) MQGreen else MQRed
            )

            if (answeredQuestions > 0) {
                Button(
                    onClick = onReviewClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.RADIUS_SMALL)
                ) {
                    Text(text = stringResource(R.string.btn_review_answers))
                }
            } else {
                TextHeadline(
                    text = stringResource(R.string.extras_perfect_score),
                    color = MQGreen
                )
            }
        }
    }
}

@Composable
fun TranslationReviewDialog(
    questions: List<TranslationQuestionUIM>,
    onDismiss: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    val screenHeight = windowInfo.containerDpSize.height
    val answeredQuestions = questions.filter { it.isAnswered }

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
                    text = stringResource(R.string.btn_review_answers),
                    modifier = Modifier.padding(bottom = Dimens.ELEMENTS_SPACING)
                )
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
                ) {
                    items(answeredQuestions) { question ->
                        ReviewCard(question)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReviewCard(question: TranslationQuestionUIM) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.DEFAULT_PADDING)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextHeadline(
                    text = question.phrase,
                    modifier = Modifier.weight(1f)
                )
                
                val statusIcon = if (question.isCorrect) Icons.Outlined.Check else Icons.Outlined.Close
                val statusColor = if (question.isCorrect) MQGreen else MQRed
                
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))
            
            HorizontalDivider()
            
            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))
            
            TextCaption(text = stringResource(R.string.extras_your_answer))
            Spacer(modifier = Modifier.height(Dimens.SMALL_PADDING))
            
            val answerBgColor = if (question.isCorrect) MQGreen.copy(alpha = 0.2f) else MQRed.copy(alpha = 0.2f)
            val answerContentColor = if (question.isCorrect) MQGreen else MQRed

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(Dimens.RADIUS_SMALL))
                    .background(answerBgColor)
                    .padding(horizontal = Dimens.DEFAULT_PADDING, vertical = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = question.userAnswer,
                    color = answerContentColor,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            val showOtherTranslations = question.isCorrect && question.possibleTranslations.size > 1
            val showCorrectTranslations = !question.isCorrect

            if (showOtherTranslations || showCorrectTranslations) {
                Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))

                val label = if (question.isCorrect) {
                    stringResource(R.string.feedback_other_correct_translations)
                } else {
                    stringResource(R.string.extras_correct_answer)
                }

                TextCaption(text = label)
                Spacer(modifier = Modifier.height(Dimens.SMALL_PADDING))
                
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SMALL_PADDING),
                    verticalArrangement = Arrangement.spacedBy(Dimens.SMALL_PADDING)
                ) {
                    val translationsToShow = if (question.isCorrect) {
                        question.possibleTranslations.filter { !it.equals(question.userAnswer, ignoreCase = true) }
                    } else {
                        question.possibleTranslations
                    }

                    translationsToShow.forEach { translation ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(Dimens.RADIUS_SMALL))
                                .background(MQGreen.copy(alpha = 0.2f))
                                .padding(horizontal = Dimens.DEFAULT_PADDING, vertical = 4.dp)
                        ) {
                            TextCaption(
                                text = translation,
                                color = MQGreen
                            )
                        }
                    }
                }
            }
        }
    }
}