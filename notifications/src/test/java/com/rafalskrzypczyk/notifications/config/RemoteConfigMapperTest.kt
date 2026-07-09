package com.rafalskrzypczyk.notifications.config

import com.rafalskrzypczyk.firestore.domain.models.NotificationConfigDTO
import com.rafalskrzypczyk.firestore.domain.models.NotificationTemplateDTO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RemoteConfigMapperTest {

    @Test
    fun `maps params and groups templates by type`() {
        val config = NotificationConfigDTO(
            streakMinValue = 3,
            winbackDays = listOf(5, 10),
            revisionIntervalDays = 4
        )
        val templates = listOf(
            NotificationTemplateDTO(type = "daily", text = "D1", enabled = true),
            NotificationTemplateDTO(type = "daily", text = "D2", enabled = true),
            NotificationTemplateDTO(type = "streak", text = "S {streak}", enabled = true),
            NotificationTemplateDTO(type = "revision", text = "R1", enabled = true),
            NotificationTemplateDTO(type = "winback", text = "W5", enabled = true, day = 5),
            NotificationTemplateDTO(type = "winback", text = "W10", enabled = true, day = 10)
        )

        val result = RemoteConfigMapper.build(config, templates)

        assertEquals(3, result.streakMinValue)
        assertEquals(listOf(5, 10), result.winbackDays)
        assertEquals(4, result.revisionIntervalDays)
        assertEquals(listOf("D1", "D2"), result.dailyTexts)
        assertEquals(listOf("S {streak}"), result.streakTexts)
        assertEquals(listOf("R1"), result.revisionTexts)
        assertEquals(mapOf(5 to "W5", 10 to "W10"), result.winbackTexts)
    }

    @Test
    fun `disabled and blank templates are ignored`() {
        val templates = listOf(
            NotificationTemplateDTO(type = "daily", text = "Active", enabled = true),
            NotificationTemplateDTO(type = "daily", text = "Disabled", enabled = false),
            NotificationTemplateDTO(type = "daily", text = "", enabled = true)
        )

        val result = RemoteConfigMapper.build(NotificationConfigDTO(), templates)

        assertEquals(listOf("Active"), result.dailyTexts)
    }

    @Test
    fun `empty template pools fall back to defaults`() {
        val result = RemoteConfigMapper.build(NotificationConfigDTO(), emptyList())

        assertEquals(NotificationRemoteConfig.DEFAULT.dailyTexts, result.dailyTexts)
        assertEquals(NotificationRemoteConfig.DEFAULT.streakTexts, result.streakTexts)
        assertEquals(NotificationRemoteConfig.DEFAULT.revisionTexts, result.revisionTexts)
        assertEquals(NotificationRemoteConfig.DEFAULT.winbackTexts, result.winbackTexts)
    }

    @Test
    fun `empty winback days fall back to defaults`() {
        val config = NotificationConfigDTO(winbackDays = emptyList())

        val result = RemoteConfigMapper.build(config, emptyList())

        assertEquals(NotificationRemoteConfig.DEFAULT.winbackDays, result.winbackDays)
        assertTrue(result.winbackDays.isNotEmpty())
    }
}
