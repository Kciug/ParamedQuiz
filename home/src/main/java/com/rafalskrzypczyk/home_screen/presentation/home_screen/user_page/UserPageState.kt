package com.rafalskrzypczyk.home_screen.presentation.home_screen.user_page

data class UserPageState (
    val error: String? = null,
    val userName: String = "",
    val userEmail: String = "",
    val score: String = ""
)