package com.rafalskrzypczyk.core.api_response

import com.rafalskrzypczyk.core.error.AppError

sealed interface ResponseState {
    object Idle : ResponseState
    object Success : ResponseState
    object Loading : ResponseState
    data class Error(val error: AppError) : ResponseState
}
