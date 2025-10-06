package com.rafalskrzypczyk.home_screen.presentation.user_settings

sealed interface UserSettingsUIEvents {
    object ClearState : UserSettingsUIEvents
    object SignOut : UserSettingsUIEvents
    data class DeleteAccount(val password: String) : UserSettingsUIEvents
    data class ChangePassword(
        val oldPassword: String,
        val newPassword: String,
        val newPasswordRepeat: String,
    ) : UserSettingsUIEvents
    data class ChangeUsername(val newUsername: String, ) : UserSettingsUIEvents
}