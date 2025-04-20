package com.rafalskrzypczyk.home_screen.presentation.home_screen.user_settings

import com.rafalskrzypczyk.core.api_response.ResponseState

data class UserSettingsState(
    val responseState: ResponseState = ResponseState.Idle,
    val userName: String = "",
    val userEmail: String = "",
    val passwordValidationMessage: String? = null,
    val usernameValidationMessage: String? = null,
    val successConfirmAction: UserSettingsConfirmAction = UserSettingsConfirmAction.CLEAR_STATE
)
