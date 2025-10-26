package com.rafalskrzypczyk.swipe_mode.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rafalskrzypczyk.core.composables.UserPointsLabel

@Composable
fun SwipeModeQuizPanel(
    modifier: Modifier = Modifier,
    score: Int,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        UserPointsLabel(value = score, grayOutWhenZero = false)
    }
}