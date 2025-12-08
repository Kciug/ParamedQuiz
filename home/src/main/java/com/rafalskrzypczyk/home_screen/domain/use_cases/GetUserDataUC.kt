package com.rafalskrzypczyk.home_screen.domain.use_cases

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.user_management.UserData
import com.rafalskrzypczyk.core.user_management.UserManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUserDataUC @Inject constructor(
    private val userManager: UserManager
) {
    operator fun invoke(): Flow<Response<UserData?>> = flow {
        emit(Response.Loading)
        val user = userManager.getCurrentLoggedUser()
        emit(Response.Success(user))
    }
}