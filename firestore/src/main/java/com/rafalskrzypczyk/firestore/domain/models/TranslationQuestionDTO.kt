package com.rafalskrzypczyk.firestore.domain.models

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class TranslationQuestionDTO(
    val id: Long = 0,
    val phrase: String = "",
    val translations: List<String> = emptyList()
) : Serializable