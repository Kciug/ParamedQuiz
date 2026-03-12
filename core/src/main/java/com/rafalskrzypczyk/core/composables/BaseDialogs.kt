package com.rafalskrzypczyk.core.composables

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.core.ui.theme.adaptiveContentColor
import com.rafalskrzypczyk.core.utils.rememberDebouncedClick

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@Composable
fun BaseCustomDialog(
    onDismissRequest: () -> Unit,
    icon: ImageVector?,
    title: String,
    headerColor: Color = MaterialTheme.colorScheme.primary,
    headerContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    content: @Composable () -> Unit,
    buttons: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Surface(
                modifier = Modifier
                    .padding(top = if (icon != null) Dimens.ICON_BACKGROUND_SIZE_LARGE / 2 else Dimens.DEFAULT_PADDING)
                    .padding(horizontal = Dimens.DEFAULT_PADDING)
                    .fillMaxWidth()
                    .heightIn(max = screenHeight * 0.9f),
                shape = RoundedCornerShape(Dimens.RADIUS_DEFAULT),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column {
                    // Header background area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(headerColor)
                            .padding(
                                top = if (icon != null) (Dimens.ICON_BACKGROUND_SIZE_LARGE / 2 + Dimens.DEFAULT_PADDING) else Dimens.DEFAULT_PADDING,
                                bottom = Dimens.DEFAULT_PADDING
                            )
                            .padding(horizontal = Dimens.DEFAULT_PADDING),
                        contentAlignment = Alignment.Center
                    ) {
                        TextHeadline(
                            text = title,
                            color = headerContentColor,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Content Area - Scrollable
                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState())
                            .padding(Dimens.DEFAULT_PADDING)
                            .fillMaxWidth()
                    ) {
                        content()
                    }

                    // Buttons Area - Fixed at bottom
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.DEFAULT_PADDING)
                            .padding(bottom = Dimens.DEFAULT_PADDING),
                        horizontalArrangement = Arrangement.End
                    ) {
                        buttons()
                    }
                }
            }

            // Floating Icon
            if (icon != null) {
                Surface(
                    modifier = Modifier
                        .size(Dimens.ICON_BACKGROUND_SIZE_LARGE)
                        .align(Alignment.TopCenter),
                    shape = CircleShape,
                    color = headerColor,
                    shadowElevation = Dimens.ELEVATION
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(Dimens.IMAGE_SIZE_SMALL),
                            tint = headerContentColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorDialog(
    errorMessage: String,
    onInteraction: () -> Unit
) {
    BaseCustomDialog(
        onDismissRequest = {}, // Modal, must interact
        icon = Icons.Outlined.ErrorOutline,
        title = stringResource(id = R.string.title_error_dialog),
        headerColor = MaterialTheme.colorScheme.error,
        headerContentColor = MaterialTheme.colorScheme.onError,
        content = {
            TextPrimary(
                text = errorMessage,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        buttons = {
            TextButton(onClick = rememberDebouncedClick(onClick = onInteraction)) {
                TextPrimary(text = stringResource(R.string.btn_confirm_OK), color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}

@Composable
fun ConfirmationDialog(
    title: String,
    message: String? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    BaseCustomDialog(
        onDismissRequest = onDismiss,
        icon = Icons.AutoMirrored.Default.HelpOutline,
        title = title,
        content = {
            if (message != null) {
                TextPrimary(
                    text = message,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        },
        buttons = {
            TextButton(onClick = rememberDebouncedClick(onClick = onDismiss)) {
                TextPrimary(text = stringResource(R.string.btn_confirm_negative), color = MaterialTheme.colorScheme.error)
            }
            TextButton(onClick = rememberDebouncedClick(onClick = onConfirm)) {
                TextPrimary(text = stringResource(R.string.btn_confirm_positive), color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}

@Composable
fun SettingsDialog(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    BaseCustomDialog(
        onDismissRequest = onDismiss,
        icon = null, // Settings dialog might not need an icon, or pass one if needed
        title = title,
        content = content,
        buttons = {
            TextButton(onClick = rememberDebouncedClick(onClick = onDismiss)) {
                TextPrimary(stringResource(R.string.btn_cancel), color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}

@Composable
fun InfoDialog(
    title: String,
    message: String,
    icon: ImageVector = Icons.Default.Info,
    headerColor: Color = MaterialTheme.colorScheme.primary,
    headerContentColor: Color = headerColor.adaptiveContentColor(),
    onDismiss: () -> Unit
) {
    BaseCustomDialog(
        onDismissRequest = onDismiss,
        icon = icon,
        title = title,
        headerColor = headerColor,
        headerContentColor = headerContentColor,
        content = {
            TextPrimary(
                text = message,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        buttons = {
            TextButton(onClick = rememberDebouncedClick(onClick = onDismiss)) {
                TextPrimary(text = stringResource(R.string.btn_confirm_OK), color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ErrorDialogPreview() {
    ParamedQuizTheme {
        Surface {
            ErrorDialog("Przykładowy błąd") { }
        }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ConfirmationDialogPreview() {
    ParamedQuizTheme {
        Surface {
            ConfirmationDialog(
                title = "Czy napewno?",
                message = "Należy się okreslić.",
                onConfirm = { },
                onDismiss = { }
            )
        }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun SettingsDialogPreview() {
    ParamedQuizTheme {
        Surface {
            SettingsDialog(
                title = "Ustawienia",
                onDismiss = { },
                content = { TextPrimary("Przykładowa treść") }
            )
        }
    }
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun InfoDialogPreview() {
    ParamedQuizTheme {
        Surface {
            InfoDialog(
                title = "Informacja",
                message = "To jest przykładowa informacja.",
                onDismiss = { }
            )
        }
    }
}
