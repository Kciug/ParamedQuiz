package com.rafalskrzypczyk.translation_mode.domain.repository

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.firestore.domain.models.IssueReportDTO
import com.rafalskrzypczyk.firestore.domain.models.TranslationQuestionDTO
import kotlinx.coroutines.flow.Flow

interface TranslationRepository {
    fun getTranslationQuestions(): Flow<Response<List<TranslationQuestionDTO>>>
    fun getUpdatedTranslationQuestions(): Flow<List<TranslationQuestionDTO>>
    fun sendIssueReport(report: IssueReportDTO): Flow<Response<Unit>>
}