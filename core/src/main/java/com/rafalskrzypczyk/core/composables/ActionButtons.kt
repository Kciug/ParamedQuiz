package com.rafalskrzypczyk.core.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {
    ActionButton(
        modifier = modifier,
        icon = Icons.AutoMirrored.Outlined.ArrowBack,
        description = "Back"
    ) { onNavigateBack() }
}

@Composable
fun SettingsButton(
    modifier: Modifier = Modifier,
    onNavigateToSettings: () -> Unit
) {
    ActionButton(
        modifier = modifier,
        icon = Icons.Default.Settings,
        description = "Settings"
    ) { onNavigateToSettings() }
}

@Composable
fun ExitButton(
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    ActionButton(
        modifier = modifier,
        icon = Icons.Default.Close,
        description = "Close"
    ) { onClose() }
}

