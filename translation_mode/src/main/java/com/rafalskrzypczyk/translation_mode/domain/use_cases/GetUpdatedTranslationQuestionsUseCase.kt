package com.rafalskrzypczyk.translation_mode.domain.use_cases

import com.rafalskrzypczyk.firestore.domain.models.TranslationQuestionDTO
import com.rafalskrzypczyk.translation_mode.domain.repository.TranslationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUpdatedTranslationQuestionsUseCase @Inject constructor(
    private val repository: TranslationRepository
) {
    operator fun invoke(): Flow<List<TranslationQuestionDTO>> {
        return repository.getUpdatedTranslationQuestions()
    }
}