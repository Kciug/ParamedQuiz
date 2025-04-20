package com.rafalskrzypczyk.core.api_response

sealed interface ResponseState {
    object Idle : ResponseState
    object Success : ResponseState
    object Loading : ResponseState
    data class Error(val message: String) : ResponseState
}



