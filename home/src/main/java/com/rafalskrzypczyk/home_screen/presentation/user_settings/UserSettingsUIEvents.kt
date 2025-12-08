package com.rafalskrzypczyk.home_screen.presentation.user_settings

import android.content.Context

sealed interface UserSettingsUIEvents {
    object ClearState : UserSettingsUIEvents
    object SignOut : UserSettingsUIEvents
    data class DeleteAccount(val password: String) : UserSettingsUIEvents
    data class DeleteAccountForProvider(val context: Context) : UserSettingsUIEvents
    data class ChangePassword(
        val oldPassword: String,
        val newPassword: String,
        val newPasswordRepeat: String,
    ) : UserSettingsUIEvents
    data class ChangeUsername(val newUsername: String, ) : UserSettingsUIEvents
    
    // Dialog Toggles
    data class ToggleChangePasswordDialog(val show: Boolean) : UserSettingsUIEvents
    data class ToggleChangeUsernameDialog(val show: Boolean) : UserSettingsUIEvents
    data class ToggleDeleteAccountDialog(val show: Boolean) : UserSettingsUIEvents
    
    object OnSuccessToastShown : UserSettingsUIEvents
}