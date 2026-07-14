package com.rafalskrzypczyk.home_screen.presentation.notification_settings

import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.rafalskrzypczyk.core.composables.Dimens
import com.rafalskrzypczyk.core.composables.PreviewContainer
import com.rafalskrzypczyk.core.composables.SettingsCategoryCard
import com.rafalskrzypczyk.core.composables.SettingsCategoryHeader
import com.rafalskrzypczyk.core.composables.SettingsItemRow
import com.rafalskrzypczyk.core.composables.SettingsSwitchRow
import com.rafalskrzypczyk.core.composables.TimePickerDialog
import com.rafalskrzypczyk.core.composables.top_bars.NavTopBar
import com.rafalskrzypczyk.home.R
import com.rafalskrzypczyk.notifications.NotificationPermission
import com.rafalskrzypczyk.notifications.NotificationSettings

@Composable
fun NotificationSettingsScreen(
    state: NotificationSettingsState,
    onEvent: (NotificationSettingsUIEvents) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            NavTopBar(title = stringResource(R.string.notification_settings_title)) { onNavigateBack() }
        }
    ) { innerPadding ->
        val modifier = Modifier
            .padding(innerPadding)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))

        Box(modifier = modifier.fillMaxSize()) {
            NotificationSettingsContent(state = state, onEvent = onEvent)
        }
    }

    if (state.showTimePickerDialog) {
        TimePickerDialog(
            title = stringResource(R.string.title_reminder_time),
            initialHour = state.reminderHour,
            initialMinute = state.reminderMinute,
            onConfirm = { hour, minute -> onEvent(NotificationSettingsUIEvents.SetReminderTime(hour, minute)) },
            onDismiss = { onEvent(NotificationSettingsUIEvents.ToggleTimePickerDialog(false)) }
        )
    }
}

@Composable
private fun NotificationSettingsContent(
    modifier: Modifier = Modifier,
    state: NotificationSettingsState,
    onEvent: (NotificationSettingsUIEvents) -> Unit
) {
    val context = LocalContext.current
    var systemEnabled by remember { mutableStateOf(NotificationPermission.areNotificationsEnabled(context)) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        systemEnabled = NotificationPermission.areNotificationsEnabled(context)
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        systemEnabled = NotificationPermission.areNotificationsEnabled(context)
        if (granted) onEvent(NotificationSettingsUIEvents.SetNotificationsEnabled(true))
    }

    val onNotificationsToggle: (Boolean) -> Unit = { enabled ->
        if (enabled) {
            onEvent(NotificationSettingsUIEvents.SetNotificationsEnabled(true))
            if (!systemEnabled) {
                val permissionNotGranted = NotificationPermission.requiresRuntimePermission() &&
                    ContextCompat.checkSelfPermission(
                        context,
                        NotificationPermission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                if (permissionNotGranted) {
                    notificationPermissionLauncher.launch(NotificationPermission.POST_NOTIFICATIONS)
                } else {
                    // Zablokowane na poziomie systemu — kierujemy do ustawień systemowych.
                    NotificationSettings.openAppNotificationSettings(context)
                }
            }
        } else {
            onEvent(NotificationSettingsUIEvents.SetNotificationsEnabled(false))
        }
    }

    val masterEnabled = state.notificationsEnabled && systemEnabled

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = Dimens.DEFAULT_PADDING)
    ) {
        SettingsCategoryHeader(stringResource(R.string.settings_notifications))

        SettingsCategoryCard {
            SettingsSwitchRow(
                title = stringResource(R.string.settings_notifications),
                icon = Icons.Outlined.Notifications,
                checked = masterEnabled,
                onCheckedChange = onNotificationsToggle
            )

            SettingsItemRow(
                title = stringResource(R.string.settings_system_notifications),
                icon = Icons.Outlined.Settings,
                onClick = { NotificationSettings.openAppNotificationSettings(context) }
            )
        }

        if (masterEnabled) {
            SettingsCategoryHeader(stringResource(R.string.notification_category_header))

            SettingsCategoryCard {
                SettingsSwitchRow(
                    title = stringResource(R.string.notification_category_reminders),
                    icon = Icons.Outlined.Alarm,
                    checked = state.remindersEnabled,
                    onCheckedChange = { onEvent(NotificationSettingsUIEvents.SetRemindersEnabled(it)) }
                )

                if (state.remindersEnabled) {
                    SettingsItemRow(
                        title = stringResource(R.string.settings_reminder_time),
                        icon = Icons.Outlined.Schedule,
                        value = formatReminderTime(state.reminderHour, state.reminderMinute),
                        showChevron = false,
                        onClick = { onEvent(NotificationSettingsUIEvents.ToggleTimePickerDialog(true)) }
                    )
                }

                SettingsSwitchRow(
                    title = stringResource(R.string.notification_category_news),
                    icon = Icons.Outlined.Campaign,
                    checked = state.newsEnabled,
                    onCheckedChange = { onEvent(NotificationSettingsUIEvents.SetNewsEnabled(it)) }
                )

                SettingsSwitchRow(
                    title = stringResource(R.string.notification_category_marketing),
                    icon = Icons.Outlined.LocalOffer,
                    checked = state.marketingEnabled,
                    onCheckedChange = { onEvent(NotificationSettingsUIEvents.SetMarketingEnabled(it)) }
                )
            }
        }
    }
}

private fun formatReminderTime(hour: Int, minute: Int): String =
    "%02d:%02d".format(hour, minute)

@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun NotificationSettingsPreview() {
    PreviewContainer {
        NotificationSettingsScreen(
            state = NotificationSettingsState(notificationsEnabled = true),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}
