package com.rafalskrzypczyk.revisions.presentation.quiz.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.core.ui.theme.MQRed
import com.rafalskrzypczyk.revisions.R
import com.rafalskrzypczyk.revisions.domain.models.RevisionQuestion

@Composable
fun RevisionsQuizFinishedExtras(
    modeName: String,
    questions: List<RevisionQuestion>,
    attemptedQuestionIds: Set<Long>,
    correctCount: Int,
    onReviewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val attemptedCount = questions.count { attemptedQuestionIds.contains(it.id) }
    val accuracy = if (attemptedCount > 0) ((correctCount.toFloat() / attemptedCount) * 100).toInt() else 0

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
                text = modeName,
                color = MaterialTheme.colorScheme.onSurface
            )

            TextPrimary(
                text = stringResource(R.string.revisions_quiz_finished_accuracy, accuracy),
                color = if (accuracy >= 50) MQGreen else MQRed
            )

            if (attemptedCount > 0) {
                Button(
                    onClick = onReviewClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.RADIUS_SMALL)
                ) {
                    Text(text = stringResource(R.string.revisions_quiz_finished_review_btn))
                }
            }
        }
    }
}
