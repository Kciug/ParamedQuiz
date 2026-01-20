package com.rafalskrzypczyk.translation_mode.domain.use_cases

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.firestore.domain.models.IssueReportDTO
import com.rafalskrzypczyk.translation_mode.domain.repository.TranslationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendTranslationReportUseCase @Inject constructor(
    private val repository: TranslationRepository
) {
    operator fun invoke(report: IssueReportDTO): Flow<Response<Unit>> {
        return repository.sendIssueReport(report)
    }
}