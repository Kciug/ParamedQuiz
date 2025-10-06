package com.rafalskrzypczyk.home_screen.presentation.user_page

sealed interface UserPageUIEvents {
    object RefreshUserData : UserPageUIEvents
    object OnNextMode : UserPageUIEvents
    object OnPreviousMode : UserPageUIEvents
}