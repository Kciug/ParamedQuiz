package com.rafalskrzypczyk.home_screen.domain.use_cases

import android.content.Context
import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.core.api_response.Response
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteAccountForProviderUC @Inject constructor(
    private val authRepository: AuthRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(context: Context? = null): Flow<Response<Unit>> {
        if (context == null) return flow { emit(Response.Error("Context required for re-authentication")) }
        
        return authRepository.reauthenticateWithProvider(context).flatMapConcat { reauthResponse ->
             when (reauthResponse) {
                is Response.Success -> authRepository.deleteUser()
                is Response.Error -> flow { emit(Response.Error(reauthResponse.error)) }
                Response.Loading -> flow { emit(Response.Loading) }
            }
        }
    }
}