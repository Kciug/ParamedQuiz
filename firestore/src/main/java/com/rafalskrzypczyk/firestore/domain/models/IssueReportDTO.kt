package com.rafalskrzypczyk.firestore.domain.models

data class IssueReportDTO(
    val id: String = "",
    val questionId: String = "",
    val questionContent: String = "",
    val description: String = "",
    val gameMode: String = "",
    val timestamp: Long = System.currentTimeMillis()
)