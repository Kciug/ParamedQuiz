package com.rafalskrzypczyk.home_screen.presentation.notification_settings

import androidx.compose.runtime.Immutable

@Immutable
data class NotificationSettingsState(
    val notificationsEnabled: Boolean = false,
    val remindersEnabled: Boolean = true,
    val newsEnabled: Boolean = true,
    val marketingEnabled: Boolean = true,
    val reminderHour: Int = 19,
    val reminderMinute: Int = 0,
    val showTimePickerDialog: Boolean = false
)
