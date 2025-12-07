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
    showBackground: Boolean = true,
    onNavigateBack: () -> Unit
) {
    ActionButton(
        modifier = modifier,
        icon = Icons.AutoMirrored.Outlined.ArrowBack,
        description = "Back",
        showBackground = showBackground
    ) { onNavigateBack() }
}

@Composable
fun SettingsButton(
    modifier: Modifier = Modifier,
    showBackground: Boolean = true,
    onNavigateToSettings: () -> Unit
) {
    ActionButton(
        modifier = modifier,
        icon = Icons.Default.Settings,
        description = "Settings",
        showBackground = showBackground
    ) { onNavigateToSettings() }
}

@Composable
fun ExitButton(
    modifier: Modifier = Modifier,
    showBackground: Boolean = true,
    onClose: () -> Unit
) {
    ActionButton(
        modifier = modifier,
        icon = Icons.Default.Close,
        description = "Close",
        showBackground = showBackground
    ) { onClose() }
}

