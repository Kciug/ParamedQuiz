package com.rafalskrzypczyk.notifications.config

import com.rafalskrzypczyk.firestore.domain.models.NotificationConfigDTO
import com.rafalskrzypczyk.firestore.domain.models.NotificationTemplateDTO

/**
 * Czysta transformacja danych z Firestore na [NotificationRemoteConfig].
 * Filtruje wyłączone szablony, grupuje po typie, a puste pule zastępuje wartościami z [NotificationRemoteConfig.DEFAULT].
 */
object RemoteConfigMapper {
    const val TYPE_DAILY = "daily"
    const val TYPE_STREAK = "streak"
    const val TYPE_REVISION = "revision"
    const val TYPE_WINBACK = "winback"

    fun build(
        config: NotificationConfigDTO,
        templates: List<NotificationTemplateDTO>
    ): NotificationRemoteConfig {
        val enabled = templates.filter { it.enabled && it.text.isNotBlank() }
        val default = NotificationRemoteConfig.DEFAULT

        fun textsFor(type: String) = enabled.filter { it.type == type }.map { it.text }

        val winback = enabled.filter { it.type == TYPE_WINBACK }
            .associate { it.day.toInt() to it.text }

        return NotificationRemoteConfig(
            streakMinValue = config.streakMinValue.toInt(),
            winbackDays = config.winbackDays.map { it.toInt() }.ifEmpty { default.winbackDays },
            revisionIntervalDays = config.revisionIntervalDays.toInt(),
            dailyTexts = textsFor(TYPE_DAILY).ifEmpty { default.dailyTexts },
            streakTexts = textsFor(TYPE_STREAK).ifEmpty { default.streakTexts },
            revisionTexts = textsFor(TYPE_REVISION).ifEmpty { default.revisionTexts },
            winbackTexts = winback.ifEmpty { default.winbackTexts }
        )
    }
}
