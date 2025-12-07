package com.rafalskrzypczyk.main_mode.presentation.quiz_base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.core.ui.theme.MQRed
import com.rafalskrzypczyk.main_mode.R

@Composable
fun QuizSubmittedSection(
    bottomPadding: Dp,
    isAnswerCorrect: Boolean,
    correctAnswers: List<String>,
    onNextQuestion: () -> Unit
) {
    val verifiedIcon = if(isAnswerCorrect) Icons.Default.Check else Icons.Default.Close
    val verifiedText = stringResource(if(isAnswerCorrect) R.string.answer_correct else R.string.answer_incorrect)
    val verifiedColor = if(isAnswerCorrect) MQGreen else MQRed

    Column(modifier = Modifier
        .fillMaxWidth()
        .clip(
            shape = RoundedCornerShape(
                topStart = Dimens.RADIUS_SMALL,
                topEnd = Dimens.RADIUS_SMALL
            )
        )
        .background(MaterialTheme.colorScheme.surfaceContainer)
        .padding(bottom = bottomPadding)
        .padding(top = Dimens.DEFAULT_PADDING)
        .padding(horizontal = Dimens.DEFAULT_PADDING)
    ) {
        Row {
            Icon(
                imageVector = verifiedIcon,
                contentDescription = stringResource(R.string.ic_desc_answer_correctness),
                tint = verifiedColor
            )
            Spacer(modifier = Modifier.width(Dimens.ELEMENTS_SPACING_SMALL))
            TextPrimary(
                text = verifiedText,
                color = verifiedColor,
            )
        }

        if (!isAnswerCorrect) {
            Spacer(modifier = Modifier.height(Dimens.DEFAULT_PADDING))
            TextPrimary(
                text = stringResource(
                    if(correctAnswers.count() > 1) R.string.question_correct_answers_plural
                    else R.string.question_correct_answers_single
                )
            )
            correctAnswers.forEach {
                CorrectAnswerElement(text = it)
            }
        }
        Spacer(modifier = Modifier.height(Dimens.DEFAULT_PADDING))
        ButtonPrimary(
            modifier = Modifier.padding(bottom = Dimens.DEFAULT_PADDING),
            title = stringResource(R.string.btn_next_question),
            onClick = onNextQuestion
        )
    }
}

@Composable
fun CorrectAnswerElement(
    text: String
) {
    TextPrimary(
        modifier = Modifier
            .padding(Dimens.SMALL_PADDING)
            .clip(shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = Dimens.SMALL_PADDING)
            .padding(horizontal = Dimens.DEFAULT_PADDING),
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}