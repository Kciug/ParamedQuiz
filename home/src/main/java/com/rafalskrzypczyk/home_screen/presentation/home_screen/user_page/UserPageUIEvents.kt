package com.rafalskrzypczyk.home_screen.presentation.home_screen.user_page

sealed interface UserPageUIEvents {
    object RefreshUserData : UserPageUIEvents
}