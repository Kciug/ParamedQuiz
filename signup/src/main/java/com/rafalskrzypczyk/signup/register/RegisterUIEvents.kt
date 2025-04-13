package com.rafalskrzypczyk.signup.register

sealed interface RegisterUIEvents{
    object ClearError : RegisterUIEvents
    data class RegisterWithCredentials(
        val name: String,
        val email: String,
        val password: String,
    ) : RegisterUIEvents
}