package com.rafalskrzypczyk.firestore.domain.models

import androidx.annotation.Keep

/** Dokument `app_config/notifications_config`. Wartości domyślne = fallback dla brakujących pól. */
@Keep
data class NotificationConfigDTO(
    val streakMinValue: Long = 2,
    val winbackDays: List<Long> = listOf(7, 14),
    val revisionIntervalDays: Long = 3,
    // Zarezerwowane (godzina przypomnień jest sterowana przez usera) — obecnie nieużywane.
    val defaultReminderHour: Long = 19
)
