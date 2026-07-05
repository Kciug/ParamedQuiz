package com.rafalskrzypczyk.revisions.presentation.quiz.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.core.ui.theme.MQRed
import com.rafalskrzypczyk.core.ui.theme.MQYellow
import com.rafalskrzypczyk.revisions.R
import com.rafalskrzypczyk.revisions.domain.models.RevisionQuestion

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RevisionsReviewCard(
    question: RevisionQuestion,
    isFailed: Boolean,
    isUnfinished: Boolean
) {
    val statusColor = when {
        isFailed -> MQRed
        isUnfinished -> MQYellow
        else -> MQGreen
    }
    val statusIcon = when {
        isFailed -> Icons.Outlined.Close
        isUnfinished -> Icons.Outlined.Warning
        else -> Icons.Outlined.Check
    }
    val statusLabel = when {
        isFailed -> stringResource(R.string.revisions_review_failed_limit)
        isUnfinished -> stringResource(R.string.revisions_review_unfinished)
        else -> stringResource(R.string.revisions_review_correct)
    }

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
                    text = question.text,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))

            Surface(
                shape = RoundedCornerShape(Dimens.RADIUS_SMALL),
                color = statusColor.copy(alpha = 0.15f)
            ) {
                TextPrimary(
                    text = statusLabel,
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING))

            when (question) {
                is RevisionQuestion.Main -> {
                    TextCaption(text = stringResource(R.string.revisions_review_correct_answers))
                    Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
                    val correctTexts = question.question.answers.filter { it.isCorrect }.map { it.answerText }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        correctTexts.forEach { text ->
                            TextPrimary(text = "• $text", fontWeight = FontWeight.Bold, color = MQGreen)
                        }
                    }
                }
                is RevisionQuestion.Cem -> {
                    TextCaption(text = stringResource(R.string.revisions_review_correct_answers))
                    Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
                    val correctTexts = question.question.answers.filter { it.isCorrect }.map { it.answerText }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        correctTexts.forEach { text ->
                            TextPrimary(text = "• $text", fontWeight = FontWeight.Bold, color = MQGreen)
                        }
                    }
                }
                is RevisionQuestion.Translation -> {
                    TextCaption(text = stringResource(R.string.revisions_review_correct_answers))
                    Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        question.question.translations.forEach { translation ->
                            Surface(
                                shape = RoundedCornerShape(Dimens.RADIUS_INNER_DEFAULT),
                                color = MQGreen.copy(alpha = 0.15f)
                            ) {
                                TextPrimary(
                                    text = translation,
                                    color = MQGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
