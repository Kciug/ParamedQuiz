package com.rafalskrzypczyk.translation_mode.presentation.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextCaption
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.core.ui.theme.MQRed
import com.rafalskrzypczyk.translation_mode.R
import com.rafalskrzypczyk.translation_mode.domain.TranslationQuestionUIM

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TranslationFeedbackPanel(
    question: TranslationQuestionUIM,
    onNext: () -> Unit,
    bottomPadding: Dp
) {
    val isCorrect = question.isCorrect
    val verifiedIcon = if(isCorrect) Icons.Default.Check else Icons.Default.Close
    val verifiedText = stringResource(if(isCorrect) R.string.feedback_correct else R.string.feedback_incorrect)
    val verifiedColor = if(isCorrect) MQGreen else MQRed
    val backgroundColor = MaterialTheme.colorScheme.surfaceContainer

    val translationsToShow = if (isCorrect) {
        question.possibleTranslations.filter { !it.equals(question.userAnswer, ignoreCase = true) }
    } else {
        question.possibleTranslations
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .clip(
            shape = RoundedCornerShape(
                topStart = Dimens.RADIUS_SMALL,
                topEnd = Dimens.RADIUS_SMALL
            )
        )
        .background(backgroundColor)
        .padding(top = Dimens.DEFAULT_PADDING)
        .padding(horizontal = Dimens.DEFAULT_PADDING)
        .padding(bottom = bottomPadding)
    ) {
        Row {
            Icon(
                imageVector = verifiedIcon,
                contentDescription = null,
                tint = verifiedColor
            )
            Spacer(modifier = Modifier.width(Dimens.ELEMENTS_SPACING_SMALL))
            TextPrimary(
                text = verifiedText,
                color = verifiedColor,
            )
        }

        if (translationsToShow.isNotEmpty()) {
            val label = if (isCorrect) R.string.feedback_other_correct_translations else R.string.feedback_correct_translations
            Spacer(modifier = Modifier.height(Dimens.DEFAULT_PADDING))
            TextPrimary(text = stringResource(label))
            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
            
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Dimens.SMALL_PADDING),
                verticalArrangement = Arrangement.spacedBy(Dimens.SMALL_PADDING)
            ) {
                translationsToShow.forEach { translation ->
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
        
        Spacer(modifier = Modifier.height(Dimens.DEFAULT_PADDING))
        
        ButtonPrimary(
            modifier = Modifier.padding(bottom = Dimens.DEFAULT_PADDING),
            title = stringResource(R.string.btn_continue),
            onClick = onNext
        )
    }
}