package com.rafalskrzypczyk.home_screen.presentation.user_settings

import androidx.compose.runtime.Immutable
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.user_management.UserAuthenticationMethod

@Immutable
data class UserSettingsState(
    val responseState: ResponseState = ResponseState.Idle,
    val accountType: UserAuthenticationMethod = UserAuthenticationMethod.NONPASSWORD,
    val userName: String = "",
    val userEmail: String = "",
    val isPremium: Boolean = false,
    val passwordValidationMessage: String? = null,
    val usernameValidationMessage: String? = null,
    val successConfirmAction: UserSettingsConfirmAction = UserSettingsConfirmAction.CLEAR_STATE,
    
    val isAnonymous: Boolean = false,

    val showChangePasswordDialog: Boolean = false,
    val showChangeUsernameDialog: Boolean = false,
    val showDeleteAccountDialog: Boolean = false,
    val showDeleteProgressDialog: Boolean = false,

    val showSuccessToast: Boolean = false
)