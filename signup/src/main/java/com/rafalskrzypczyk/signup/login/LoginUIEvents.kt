package com.rafalskrzypczyk.signup.login

import android.content.Context

sealed interface LoginUIEvents {
    object ClearError : LoginUIEvents
    data class LoginWithCredentials(
        val email: String,
        val password: String,
    ) : LoginUIEvents
    data class LoginWithGoogle(val context: Context) : LoginUIEvents
}