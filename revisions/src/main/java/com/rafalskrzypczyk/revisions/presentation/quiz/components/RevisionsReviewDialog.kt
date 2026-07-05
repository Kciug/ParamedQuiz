package com.rafalskrzypczyk.revisions.presentation.quiz.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.revisions.R
import com.rafalskrzypczyk.revisions.domain.models.RevisionQuestion

@Composable
fun RevisionsReviewDialog(
    questions: List<RevisionQuestion>,
    failedQuestionIds: Set<Long>,
    attemptedQuestionIds: Set<Long>,
    remainingQueueIds: Set<Long>,
    onDismiss: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    val screenHeight = windowInfo.containerDpSize.height
    val reviewedQuestions = remember(questions, attemptedQuestionIds) {
        questions.filter { attemptedQuestionIds.contains(it.id) }
    }

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
                    text = stringResource(R.string.revisions_review_title),
                    modifier = Modifier.padding(bottom = Dimens.ELEMENTS_SPACING)
                )
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
                ) {
                    items(reviewedQuestions) { question ->
                        val isFailed = failedQuestionIds.contains(question.id)
                        val isUnfinished = remainingQueueIds.contains(question.id)
                        RevisionsReviewCard(
                            question = question,
                            isFailed = isFailed,
                            isUnfinished = isUnfinished
                        )
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
