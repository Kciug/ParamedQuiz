package com.rafalskrzypczyk.signup

import androidx.compose.runtime.Immutable
import com.rafalskrzypczyk.core.error.AppError

@Immutable
data class AuthenticationState(
    val isLoading: Boolean = false,
    val error: AppError? = null,
    val isSuccess: Boolean = false,
)
