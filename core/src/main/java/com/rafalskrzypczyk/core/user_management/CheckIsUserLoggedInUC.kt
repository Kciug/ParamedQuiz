package com.rafalskrzypczyk.core.user_management

import javax.inject.Inject

class CheckIsUserLoggedInUC @Inject constructor(
    private val userManager: UserManager
){
    operator fun invoke(): Boolean = userManager.getCurrentLoggedUser() != null
}