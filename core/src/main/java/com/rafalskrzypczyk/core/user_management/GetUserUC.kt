package com.rafalskrzypczyk.core.user_management

import javax.inject.Inject

class GetUserUC @Inject constructor(
    private val userManager: UserManager
) {
    operator fun invoke(): UserData? {
        return userManager.getCurrentLoggedUser()
    }
}