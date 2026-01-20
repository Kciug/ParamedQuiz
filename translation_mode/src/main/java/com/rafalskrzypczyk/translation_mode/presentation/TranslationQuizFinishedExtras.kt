package com.rafalskrzypczyk.translation_mode.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.composables.TextTitle
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.core.ui.theme.MQRed
import com.rafalskrzypczyk.translation_mode.R
import com.rafalskrzypczyk.translation_mode.domain.TranslationQuestionUIM

private val LABEL_WIDTH = 60.dp
private val ICON_SIZE = 16.dp

@Composable
fun TranslationQuizFinishedExtras(
    questions: List<TranslationQuestionUIM>,
    modifier: Modifier = Modifier
) {
    val wrongAnswers = questions.filter { !it.isCorrect && it.isAnswered }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.DEFAULT_PADDING)
    ) {
        if (wrongAnswers.isEmpty()) {
            PerfectScoreCard()
        } else {
            TextHeadline(
                text = stringResource(R.string.extras_review_title),
                modifier = Modifier.padding(horizontal = Dimens.DEFAULT_PADDING)
            )
            wrongAnswers.forEach { question ->
                ReviewCard(question)
            }
        }
    }
}

@Composable
private fun PerfectScoreCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MQGreen.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.DEFAULT_PADDING),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = MQGreen
            )
            Spacer(modifier = Modifier.width(Dimens.ELEMENTS_SPACING))
            TextTitle(
                text = stringResource(R.string.extras_perfect_score),
                color = MQGreen
            )
        }
    }
}

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
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextPrimary(
                    text = stringResource(R.string.extras_your_answer),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(LABEL_WIDTH)
                )
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = MQRed,
                    modifier = Modifier.height(ICON_SIZE)
                )
                Spacer(modifier = Modifier.width(Dimens.SMALL_PADDING))
                TextPrimary(
                    text = question.userAnswer,
                    color = MQRed,
                    textDecoration = TextDecoration.LineThrough
                )
            }

            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))

            Row(verticalAlignment = Alignment.Top) {
                TextPrimary(
                    text = stringResource(R.string.extras_correct_answer),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(LABEL_WIDTH)
                )
                Column {
                    question.possibleTranslations.forEach { translation ->
                        TextPrimary(
                            text = translation,
                            color = MQGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}