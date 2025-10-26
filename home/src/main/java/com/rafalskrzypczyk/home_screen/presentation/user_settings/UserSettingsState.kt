package com.rafalskrzypczyk.home_screen.presentation.user_settings

import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.user_management.UserAuthenticationMethod

data class UserSettingsState(
    val responseState: ResponseState = ResponseState.Idle,
    val accountType: UserAuthenticationMethod = UserAuthenticationMethod.NONPASSWORD,
    val userName: String = "",
    val userEmail: String = "",
    val passwordValidationMessage: String? = null,
    val usernameValidationMessage: String? = null,
    val successConfirmAction: UserSettingsConfirmAction = UserSettingsConfirmAction.CLEAR_STATE
)
