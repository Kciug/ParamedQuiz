package com.rafalskrzypczyk.home_screen.presentation.user_settings

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.rafalskrzypczyk.core.composables.BaseCustomDialog
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.PreviewContainer
import com.rafalskrzypczyk.core.composables.SettingsSwitchRow
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.core.utils.rememberDebouncedClick
import com.rafalskrzypczyk.home.R

/**
 * Opis zgody na analitykę razem z przełącznikiem — w dialogu, żeby nie zaśmiecać listy ustawień.
 * Przełącznik działa od razu (jak wcześniej w liście), przycisk tylko zamyka dialog.
 */
@Composable
fun UserSettingsAnalyticsDialog(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    BaseCustomDialog(
        onDismissRequest = onDismiss,
        icon = Icons.Outlined.Insights,
        title = stringResource(R.string.settings_analytics),
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING)
            ) {
                TextPrimary(
                    text = stringResource(R.string.settings_analytics_info),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                SettingsSwitchRow(
                    title = stringResource(R.string.settings_analytics_switch),
                    checked = enabled,
                    onCheckedChange = onEnabledChange,
                    modifier = Modifier
                        .clip(RoundedCornerShape(Dimens.RADIUS_SMALL))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        },
        buttons = {
            TextButton(onClick = rememberDebouncedClick(onClick = onDismiss)) {
                TextPrimary(
                    text = stringResource(com.rafalskrzypczyk.core.R.string.btn_confirm_OK),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun UserSettingsAnalyticsDialogPreview() {
    PreviewContainer {
        UserSettingsAnalyticsDialog(
            enabled = true,
            onEnabledChange = {},
            onDismiss = {}
        )
    }
}
