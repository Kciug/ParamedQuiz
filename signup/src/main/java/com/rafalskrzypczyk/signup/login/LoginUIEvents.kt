package com.rafalskrzypczyk.signup.login

sealed interface LoginUIEvents {
    data class LoginWithCredentials(
        val email: String,
        val password: String,
    ) : LoginUIEvents
}