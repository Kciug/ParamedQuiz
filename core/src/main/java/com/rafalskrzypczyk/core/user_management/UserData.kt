package com.rafalskrzypczyk.core.user_management

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val id: String,
    val email: String,
    val name: String,
    val authenticationMethod: UserAuthenticationMethod
)
