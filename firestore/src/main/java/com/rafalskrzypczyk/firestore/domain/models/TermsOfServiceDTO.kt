package com.rafalskrzypczyk.firestore.domain.models

import androidx.annotation.Keep

@Keep
data class TermsOfServiceDTO(
    val version: Int = 0,
    val content: String = "",
    val isMandatory: Boolean = false
)
