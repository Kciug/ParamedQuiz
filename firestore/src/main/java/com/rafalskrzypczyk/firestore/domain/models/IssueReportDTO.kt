package com.rafalskrzypczyk.firestore.domain.models

import androidx.annotation.Keep

@Keep
data class IssueReportDTO(
    val id: String = "",
    val questionId: Long = -1,
    val questionContent: String = "",
    val description: String = "",
    val gameMode: String = "",
    val timestamp: Long = System.currentTimeMillis()
)