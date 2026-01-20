package com.rafalskrzypczyk.translation_mode.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.rafalskrzypczyk.core.composables.ButtonPrimary
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextHeadline
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.ui.theme.MQGreen
import com.rafalskrzypczyk.core.ui.theme.MQRed
import com.rafalskrzypczyk.translation_mode.domain.TranslationQuestionUIM

@Composable
fun TranslationFeedbackPanel(
    question: TranslationQuestionUIM,
    onNext: () -> Unit
) {
    val isCorrect = question.isCorrect
    val verifiedIcon = if(isCorrect) Icons.Default.Check else Icons.Default.Close
    val verifiedText = if(isCorrect) "Correct!" else "Incorrect"
    val verifiedColor = if(isCorrect) MQGreen else MQRed
    val backgroundColor = MaterialTheme.colorScheme.surfaceContainer

    Column(modifier = Modifier
        .fillMaxWidth()
        .clip(
            shape = RoundedCornerShape(
                topStart = Dimens.RADIUS_SMALL,
                topEnd = Dimens.RADIUS_SMALL
            )
        )
        .background(backgroundColor)
        .padding(Dimens.DEFAULT_PADDING)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = verifiedIcon,
                contentDescription = null,
                tint = verifiedColor,
                modifier = Modifier.padding(end = Dimens.ELEMENTS_SPACING_SMALL)
            )
            TextHeadline(
                text = verifiedText,
                color = verifiedColor,
            )
        }

        if (!isCorrect) {
            Spacer(modifier = Modifier.height(Dimens.DEFAULT_PADDING))
            TextPrimary(text = "Correct translations:")
            Spacer(modifier = Modifier.height(Dimens.ELEMENTS_SPACING_SMALL))
            
            question.possibleTranslations.forEach {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = Dimens.SMALL_PADDING)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Dimens.LARGE_PADDING))
        
        ButtonPrimary(
            title = "Continue",
            onClick = onNext
        )
    }
}