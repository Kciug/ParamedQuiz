package com.rafalskrzypczyk.core.report_issues

data class IssueReport(
    val id: String = "",
    val questionId: String = "",
    val questionContent: String = "",
    val description: String = "",
    val gameMode: String = "",
    val timestamp: Long? = null
)