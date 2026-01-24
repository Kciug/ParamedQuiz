package com.rafalskrzypczyk.firestore.domain.use_cases

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.firestore.domain.models.TermsOfServiceDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTermsOfServiceUC @Inject constructor(
    private val firestoreApi: FirestoreApi
) {
    operator fun invoke(): Flow<Response<TermsOfServiceDTO>> {
        return firestoreApi.getTermsOfService()
    }
}
