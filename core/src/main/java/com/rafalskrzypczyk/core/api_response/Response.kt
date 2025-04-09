package com.rafalskrzypczyk.core.api_response

sealed interface Response<out D> {
    data class Success<out D>(val data: D) : Response<D>
    data class Error(val error: String) : Response<Nothing>
    object Loading : Response<Nothing>
}