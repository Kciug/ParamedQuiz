package com.rafalskrzypczyk.home_screen.presentation.home_page.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.rafalskrzypczyk.core.composables.BaseCustomDialog
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.TextPrimary
import com.rafalskrzypczyk.home.R

/**
 * Pre-permission prompt (priming) dla powiadomień — pokazywany po pierwszej ukończonej sesji,
 * zanim poprosimy o systemowe uprawnienie.
 */
@Composable
fun NotificationConsentDialog(
    onEnable: () -> Unit,
    onDismiss: () -> Unit
) {
    BaseCustomDialog(
        onDismissRequest = onDismiss,
        icon = Icons.Outlined.Notifications,
        title = stringResource(R.string.notification_consent_title),
        content = {
            TextPrimary(
                text = stringResource(R.string.notification_consent_message),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        buttons = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.ELEMENTS_SPACING_SMALL),
                horizontalAlignment = Alignment.End
            ) {
                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    TextPrimary(
                        text = stringResource(R.string.notification_consent_later),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                TextButton(onClick = onEnable, modifier = Modifier.fillMaxWidth()) {
                    TextPrimary(
                        text = stringResource(R.string.notification_consent_enable),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    )
}
