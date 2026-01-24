package com.rafalskrzypczyk.firestore.domain.models

sealed interface TermsOfServiceStatus {
    data object Loading : TermsOfServiceStatus
    data object Accepted : TermsOfServiceStatus
    data class NeedsAcceptance(val terms: TermsOfServiceDTO) : TermsOfServiceStatus
    data class Error(val message: String) : TermsOfServiceStatus
}
