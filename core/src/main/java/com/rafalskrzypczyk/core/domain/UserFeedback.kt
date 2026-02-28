package com.rafalskrzypczyk.core.domain

data class UserFeedback(
    val id: String = "",
    val userId: String? = null,
    val feedback: String,
    val rating: Int,
    val timestamp: Long = System.currentTimeMillis()
)
