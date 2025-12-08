package com.rafalskrzypczyk.home_screen.domain.use_cases

import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.core.api_response.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UpdateUsernameUC @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(newUsername: String): Flow<Response<Unit>> {
        return authRepository.changeUserName(newUsername).map { response ->
            when (response) {
                is Response.Success -> Response.Success(Unit)
                is Response.Error -> Response.Error(response.error)
                Response.Loading -> Response.Loading
            }
        }
    }
}