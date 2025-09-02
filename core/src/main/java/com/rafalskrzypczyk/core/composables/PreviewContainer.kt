package com.rafalskrzypczyk.core.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme

@Composable
fun PreviewContainer(content: @Composable () -> Unit) {
    ParamedQuizTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            content()
        }
    }
}