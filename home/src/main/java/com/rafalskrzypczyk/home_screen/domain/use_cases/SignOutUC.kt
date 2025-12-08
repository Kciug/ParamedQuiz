package com.rafalskrzypczyk.home_screen.domain.use_cases

import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.core.user_management.UserManager
import javax.inject.Inject

class SignOutUC @Inject constructor(
    private val authRepository: AuthRepository,
    private val userManager: UserManager
) {
    operator fun invoke() {
        authRepository.signOut()
        userManager.clearUserDataLocal()
    }
}