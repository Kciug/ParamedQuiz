package com.rafalskrzypczyk.auth.domain

import com.rafalskrzypczyk.core.user_management.UserAuthenticationMethod
import com.rafalskrzypczyk.core.user_management.UserData
import com.rafalskrzypczyk.firestore.domain.models.UserDataDTO

fun UserDataDTO.toDomain(email: String, authMethod: UserAuthenticationMethod) = UserData(
    id = id,
    email = email,
    name = name,
    authenticationMethod = authMethod
)

fun UserData.toDTO() = UserDataDTO(
    id = id,
    name = name
)