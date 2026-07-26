package com.rafalskrzypczyk.firestore.domain.models

import com.rafalskrzypczyk.core.error.AppError

sealed interface TermsOfServiceStatus {
    data object Loading : TermsOfServiceStatus
    data object Accepted : TermsOfServiceStatus
    data class NeedsAcceptance(val terms: TermsOfServiceDTO) : TermsOfServiceStatus
    data class Error(val error: AppError) : TermsOfServiceStatus
}
