package com.rafalskrzypczyk.core.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.SwipeVertical
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.rafalskrzypczyk.core.ui.theme.ModeCem
import com.rafalskrzypczyk.core.ui.theme.ModeMain
import com.rafalskrzypczyk.core.ui.theme.ModeSwipe
import com.rafalskrzypczyk.core.ui.theme.ModeTranslation

object ModeInfoProvider {
    fun getIcon(mode: QuizMode): ImageVector {
        return when (mode) {
            QuizMode.MainMode -> Icons.Rounded.Quiz
            QuizMode.SwipeMode -> Icons.Rounded.SwipeVertical
            QuizMode.TranslationMode -> Icons.Rounded.Translate
            QuizMode.CemMode -> Icons.Rounded.Quiz
        }
    }

    fun getColor(mode: QuizMode): Color {
        return when (mode) {
            QuizMode.MainMode -> ModeMain
            QuizMode.SwipeMode -> ModeSwipe
            QuizMode.TranslationMode -> ModeTranslation
            QuizMode.CemMode -> ModeCem
        }
    }
}
