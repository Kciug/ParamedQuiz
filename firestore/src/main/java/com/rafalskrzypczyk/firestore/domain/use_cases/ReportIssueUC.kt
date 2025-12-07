package com.rafalskrzypczyk.firestore.domain.use_cases

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.firestore.domain.models.IssueReportDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReportIssueUC @Inject constructor(
    private val firestoreApi: FirestoreApi
) {
    operator fun invoke(report: IssueReportDTO): Flow<Response<Unit>> {
        return firestoreApi.sendIssueReport(report)
    }
}