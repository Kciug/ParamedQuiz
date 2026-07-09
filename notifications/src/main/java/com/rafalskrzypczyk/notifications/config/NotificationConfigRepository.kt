package com.rafalskrzypczyk.notifications.config

import android.content.SharedPreferences
import androidx.core.content.edit
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Dostarcza parametry i teksty powiadomień, z Firestore (odświeżane maks. raz na 7 dni),
 * cache'owane lokalnie jako JSON, z pełnym fallbackiem [NotificationRemoteConfig.DEFAULT].
 */
@Singleton
class NotificationConfigRepository @Inject constructor(
    private val firestoreApi: FirestoreApi,
    private val sharedPreferences: SharedPreferences
) {
    private val json = Json { ignoreUnknownKeys = true }

    /** Pobiera świeżą konfigurację z Firestore, jeśli minął interwał (lub [force]). Ciche na błędach. */
    suspend fun refresh(force: Boolean = false) {
        if (!force && !isStale()) return

        val configResponse = firestoreApi.getNotificationConfig().first { it !is Response.Loading }
        if (configResponse !is Response.Success) return // brak configu → zostaw poprzedni cache/fallback

        val templatesResponse = firestoreApi.getNotificationTemplates().first { it !is Response.Loading }
        val templates = (templatesResponse as? Response.Success)?.data ?: emptyList()

        val remoteConfig = RemoteConfigMapper.build(configResponse.data, templates)
        sharedPreferences.edit {
            putString(KEY_CONFIG_JSON, json.encodeToString(remoteConfig))
            putLong(KEY_LAST_CONFIG_FETCH, System.currentTimeMillis())
        }
    }

    fun current(): NotificationRemoteConfig {
        val stored = sharedPreferences.getString(KEY_CONFIG_JSON, null) ?: return NotificationRemoteConfig.DEFAULT
        return runCatching { json.decodeFromString<NotificationRemoteConfig>(stored) }
            .getOrDefault(NotificationRemoteConfig.DEFAULT)
    }

    // Parametry decyzji
    fun streakMinValue(): Int = current().streakMinValue
    fun winbackDays(): List<Int> = current().winbackDays
    fun revisionIntervalDays(): Int = current().revisionIntervalDays

    // Teksty
    fun dailyText(): String = current().dailyTexts.random()
    fun streakText(streak: Int): String = current().streakTexts.random().replace("{streak}", streak.toString())
    fun revisionText(): String = current().revisionTexts.random()
    fun winbackText(day: Int): String =
        current().winbackTexts[day]
            ?: NotificationRemoteConfig.DEFAULT.winbackTexts[day]
            ?: "👋 Wróć do nauki — Twoje pytania czekają."

    private fun isStale(): Boolean {
        val lastFetch = sharedPreferences.getLong(KEY_LAST_CONFIG_FETCH, 0L)
        if (lastFetch == 0L) return true
        return TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - lastFetch) >= REFRESH_INTERVAL_DAYS
    }

    companion object {
        private const val KEY_CONFIG_JSON = "notification_config_json"
        private const val KEY_LAST_CONFIG_FETCH = "notification_last_config_fetch"
        private const val REFRESH_INTERVAL_DAYS = 7
    }
}
