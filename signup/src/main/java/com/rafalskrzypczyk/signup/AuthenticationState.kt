package com.rafalskrzypczyk.signup

data class AuthenticationState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val authenticationSuccessfull: Boolean = false,
)
