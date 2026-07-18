package com.rafalskrzypczyk.home_screen.presentation.notification_settings

sealed interface NotificationSettingsUIEvents {
    data class SetNotificationsEnabled(val enabled: Boolean) : NotificationSettingsUIEvents
    data class SetRemindersEnabled(val enabled: Boolean) : NotificationSettingsUIEvents
    data class SetNewsEnabled(val enabled: Boolean) : NotificationSettingsUIEvents
    data class SetMarketingEnabled(val enabled: Boolean) : NotificationSettingsUIEvents
    data class SetReminderTime(val hour: Int, val minute: Int) : NotificationSettingsUIEvents
    data class ToggleTimePickerDialog(val show: Boolean) : NotificationSettingsUIEvents
}
