package com.rafalskrzypczyk.auth.domain

import com.rafalskrzypczyk.core.user_management.UserData
import com.rafalskrzypczyk.firestore.domain.models.UserDataDTO

fun UserDataDTO.toDomain(email: String) = UserData(
    id = id,
    email = email,
    name = name,
)

fun UserData.toDTO() = UserDataDTO(
    id = id,
    name = name
)