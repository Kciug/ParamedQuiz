package com.rafalskrzypczyk.translation_mode.data

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.firestore.domain.models.IssueReportDTO
import com.rafalskrzypczyk.firestore.domain.models.TranslationQuestionDTO
import com.rafalskrzypczyk.translation_mode.domain.repository.TranslationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TranslationRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi
) : TranslationRepository {
    
    override fun getTranslationQuestions(): Flow<Response<List<TranslationQuestionDTO>>> {
        return firestoreApi.getTranslationQuestions()
    }

    override fun getUpdatedTranslationQuestions(): Flow<List<TranslationQuestionDTO>> {
        return firestoreApi.getUpdatedTranslationQuestions()
    }

    override fun sendIssueReport(report: IssueReportDTO): Flow<Response<Unit>> {
        return firestoreApi.sendIssueReport(report)
    }
}