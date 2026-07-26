package com.rafalskrzypczyk.core.api_response

import com.rafalskrzypczyk.core.error.AppError

sealed interface Response<out D> {
    data class Success<out D>(val data: D) : Response<D>
    data class Error(val error: AppError) : Response<Nothing>
    object Loading : Response<Nothing>
}
