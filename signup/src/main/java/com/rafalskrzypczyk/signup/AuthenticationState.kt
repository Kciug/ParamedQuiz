package com.rafalskrzypczyk.signup

import androidx.compose.runtime.Immutable

@Immutable
data class AuthenticationState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
)
