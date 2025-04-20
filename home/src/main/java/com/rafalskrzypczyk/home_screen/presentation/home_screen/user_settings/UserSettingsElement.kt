package com.rafalskrzypczyk.home_screen.presentation.home_screen.user_settings

import androidx.compose.runtime.Composable

data class UserSettingsElement(
    val title: String,
    val content: @Composable () -> Unit,
)
