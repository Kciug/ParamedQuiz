package com.rafalskrzypczyk.firestore.domain.models

import androidx.annotation.Keep

@Keep
data class UserDataDTO (
    val id: String = "",
    val name: String = "",
)