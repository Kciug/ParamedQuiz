package com.rafalskrzypczyk.signup.reset_password

sealed interface ResetPasswordUIEvents {
    object ClearError : ResetPasswordUIEvents
    data class SendResetPasswordEmail (
        val email: String,
    ) : ResetPasswordUIEvents
}