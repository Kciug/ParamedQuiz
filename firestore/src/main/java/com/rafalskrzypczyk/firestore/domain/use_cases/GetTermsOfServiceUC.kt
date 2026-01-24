package com.rafalskrzypczyk.firestore.domain.use_cases

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.firestore.domain.models.TermsOfServiceStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTermsOfServiceUC @Inject constructor(
    private val firestoreApi: FirestoreApi,
    private val sharedPrefs: SharedPreferencesApi
) {
    operator fun invoke(forceCheck: Boolean = false): Flow<TermsOfServiceStatus> = flow {
        val acceptedVersion = sharedPrefs.getAcceptedTermsVersion()

        firestoreApi.getTermsOfServiceUpdates().collect { response ->
            when (response) {
                is Response.Loading -> emit(TermsOfServiceStatus.Loading)
                is Response.Success -> {
                    val remoteTerms = response.data
                    if (remoteTerms.version > acceptedVersion) {
                        emit(TermsOfServiceStatus.NeedsAcceptance(remoteTerms))
                    } else {
                        emit(TermsOfServiceStatus.Accepted)
                    }
                }
                is Response.Error -> {
                    if (acceptedVersion != -1) {
                        emit(TermsOfServiceStatus.Accepted)
                    } else {
                        emit(TermsOfServiceStatus.Error(response.error))
                    }
                }
            }
        }
    }
}
