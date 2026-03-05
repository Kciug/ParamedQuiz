package com.rafalskrzypczyk.translation_mode.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rafalskrzypczyk.core.composables.CorrectAnswersLabel
import com.rafalskrzypczyk.core.composables.UserPointsLabel

@Composable
fun TranslationQuizTopPanel(
    modifier: Modifier = Modifier,
    score: Int,
    correctAnswers: Int,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        UserPointsLabel(value = score, grayOutWhenZero = false)
        CorrectAnswersLabel(value = correctAnswers)
    }
}
