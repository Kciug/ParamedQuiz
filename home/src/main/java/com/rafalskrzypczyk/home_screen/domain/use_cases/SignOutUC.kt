package com.rafalskrzypczyk.home_screen.domain.use_cases

import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.core.api_response.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignOutUC @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Response<Unit>> = flow {
        emit(Response.Loading)
        authRepository.signOut()
        emit(Response.Success(Unit))
    }
}
