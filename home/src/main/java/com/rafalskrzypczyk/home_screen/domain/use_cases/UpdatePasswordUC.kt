package com.rafalskrzypczyk.home_screen.domain.use_cases

import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.user_management.UserManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdatePasswordUC @Inject constructor(
    private val authRepository: AuthRepository,
    private val userManager: UserManager
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(old: String, new: String): Flow<Response<Unit>> {
        val email = userManager.getCurrentLoggedUser()?.email ?: return flow { emit(Response.Error("User not logged in")) }
        
        return authRepository.reauthenticateWithPassword(email, old).flatMapConcat { reauthResponse ->
            when (reauthResponse) {
                is Response.Success -> authRepository.changePassword(new)
                is Response.Error -> flow { emit(Response.Error(reauthResponse.error)) }
                Response.Loading -> flow { emit(Response.Loading) }
            }
        }
    }
}