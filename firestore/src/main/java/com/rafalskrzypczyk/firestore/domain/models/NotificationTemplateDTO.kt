package com.rafalskrzypczyk.firestore.domain.models

import androidx.annotation.Keep

/** Dokument w kolekcji `notification_templates`. `type`: daily/streak/revision/winback; `day` tylko dla winback. */
@Keep
data class NotificationTemplateDTO(
    val type: String = "",
    val text: String = "",
    @field:JvmField
    val enabled: Boolean = true,
    val day: Long = 0
)
