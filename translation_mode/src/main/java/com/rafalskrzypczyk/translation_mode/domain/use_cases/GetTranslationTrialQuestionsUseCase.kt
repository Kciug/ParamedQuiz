package com.rafalskrzypczyk.translation_mode.domain.use_cases

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.firestore.domain.models.TranslationQuestionDTO
import com.rafalskrzypczyk.translation_mode.domain.repository.TranslationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTranslationTrialQuestionsUseCase @Inject constructor(
    private val repository: TranslationRepository
) {
    operator fun invoke(): Flow<Response<List<TranslationQuestionDTO>>> {
        return repository.getTranslationQuestions().map {
            if (it is Response.Success) {
                Response.Success(it.data.filter { q -> q.isFree })
            } else {
                it
            }
        }
    }
}
