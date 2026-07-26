package com.rafalskrzypczyk.home_screen.domain.use_cases

import android.content.Context
import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.error.AppError
import com.rafalskrzypczyk.core.error.ErrorLogger
import com.rafalskrzypczyk.core.error.report
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val ORIGIN_DELETE_ACCOUNT_FOR_PROVIDER = "DeleteAccountForProviderUC.invoke"

class DeleteAccountForProviderUC @Inject constructor(
    private val errorLogger: ErrorLogger,
    private val authRepository: AuthRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(context: Context? = null): Flow<Response<Unit>> {
        if (context == null) return flow { emit(Response.Error(errorLogger.report(ORIGIN_DELETE_ACCOUNT_FOR_PROVIDER, AppError.Auth.ReauthContextMissing))) }
        
        return authRepository.reauthenticateWithProvider(context).flatMapConcat { reauthResponse ->
             when (reauthResponse) {
                is Response.Success -> authRepository.deleteUser()
                is Response.Error -> flow { emit(reauthResponse) }
                Response.Loading -> flow { emit(Response.Loading) }
            }
        }
    }
}