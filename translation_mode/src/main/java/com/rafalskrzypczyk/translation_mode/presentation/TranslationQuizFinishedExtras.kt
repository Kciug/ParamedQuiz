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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
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
    val totalQuestions = questions.size
    val correctAnswers = questions.count { it.isCorrect }
    val wrongAnswersCount = totalQuestions - correctAnswers
    
    val percentage = if (totalQuestions > 0) ((correctAnswers.toFloat() / totalQuestions) * 100).toInt() else 0

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

            if (wrongAnswersCount > 0) {
                Button(
                    onClick = onReviewClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.RADIUS_SMALL)
                ) {
                    Text(text = stringResource(R.string.btn_review_errors))
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
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val wrongAnswers = questions.filter { !it.isCorrect && it.isAnswered }

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
                    text = stringResource(R.string.btn_review_errors),
                    modifier = Modifier.padding(bottom = Dimens.ELEMENTS_SPACING)
                )
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
                ) {
                    items(wrongAnswers) { question ->
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
            TextHeadline(text = question.phrase)
            
            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))
            
            TextCaption(text = stringResource(R.string.extras_your_answer))
            Spacer(modifier = Modifier.height(Dimens.SMALL_PADDING))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(Dimens.RADIUS_SMALL))
                    .background(MQRed.copy(alpha = 0.2f))
                    .padding(horizontal = Dimens.DEFAULT_PADDING, vertical = 4.dp)
            ) {
                TextCaption(
                    text = question.userAnswer,
                    color = MQRed
                )
            }

            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))

            TextCaption(text = stringResource(R.string.extras_correct_answer))
            Spacer(modifier = Modifier.height(Dimens.SMALL_PADDING))
            
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Dimens.SMALL_PADDING),
                verticalArrangement = Arrangement.spacedBy(Dimens.SMALL_PADDING)
            ) {
                question.possibleTranslations.forEach { translation ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Dimens.RADIUS_SMALL))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = Dimens.DEFAULT_PADDING, vertical = 4.dp)
                    ) {
                        TextCaption(
                            text = translation,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}