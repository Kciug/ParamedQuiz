package com.rafalskrzypczyk.home_screen.presentation.notification_settings

import androidx.lifecycle.ViewModel
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import com.rafalskrzypczyk.notifications.ContentTopicManager
import com.rafalskrzypczyk.notifications.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsVM @Inject constructor(
    private val sharedPrefs: SharedPreferencesApi,
    private val reminderScheduler: ReminderScheduler,
    private val contentTopicManager: ContentTopicManager
) : ViewModel() {
    private val _state = MutableStateFlow(NotificationSettingsState())
    val state = _state.asStateFlow()

    init {
        loadSettings()
    }

    fun onEvent(event: NotificationSettingsUIEvents) {
        when (event) {
            is NotificationSettingsUIEvents.SetNotificationsEnabled -> setNotificationsEnabled(event.enabled)
            is NotificationSettingsUIEvents.SetRemindersEnabled -> setRemindersEnabled(event.enabled)
            is NotificationSettingsUIEvents.SetNewsEnabled -> setNewsEnabled(event.enabled)
            is NotificationSettingsUIEvents.SetMarketingEnabled -> setMarketingEnabled(event.enabled)
            is NotificationSettingsUIEvents.SetReminderTime -> setReminderTime(event.hour, event.minute)
            is NotificationSettingsUIEvents.ToggleTimePickerDialog -> _state.update { it.copy(showTimePickerDialog = event.show) }
        }
    }

    private fun loadSettings() {
        _state.update {
            it.copy(
                notificationsEnabled = sharedPrefs.isNotificationsEnabled(),
                remindersEnabled = sharedPrefs.isRemindersEnabled(),
                newsEnabled = sharedPrefs.isNewsEnabled(),
                marketingEnabled = sharedPrefs.isMarketingEnabled(),
                reminderHour = sharedPrefs.getReminderHour(),
                reminderMinute = sharedPrefs.getReminderMinute()
            )
        }
    }

    private fun setNotificationsEnabled(enabled: Boolean) {
        sharedPrefs.setNotificationsEnabled(enabled)
        _state.update { it.copy(notificationsEnabled = enabled) }
        reminderScheduler.ensureScheduled()
        contentTopicManager.ensureSubscription()
    }

    private fun setRemindersEnabled(enabled: Boolean) {
        sharedPrefs.setRemindersEnabled(enabled)
        _state.update { it.copy(remindersEnabled = enabled) }
        reminderScheduler.ensureScheduled()
    }

    private fun setNewsEnabled(enabled: Boolean) {
        sharedPrefs.setNewsEnabled(enabled)
        _state.update { it.copy(newsEnabled = enabled) }
        contentTopicManager.ensureSubscription()
    }

    private fun setMarketingEnabled(enabled: Boolean) {
        sharedPrefs.setMarketingEnabled(enabled)
        _state.update { it.copy(marketingEnabled = enabled) }
        contentTopicManager.ensureSubscription()
    }

    private fun setReminderTime(hour: Int, minute: Int) {
        sharedPrefs.setReminderTime(hour, minute)
        _state.update {
            it.copy(
                reminderHour = hour,
                reminderMinute = minute,
                showTimePickerDialog = false
            )
        }
        reminderScheduler.ensureScheduled()
    }
}
