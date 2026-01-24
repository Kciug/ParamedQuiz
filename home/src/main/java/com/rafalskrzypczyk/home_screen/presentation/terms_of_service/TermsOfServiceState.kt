package com.rafalskrzypczyk.home_screen.presentation.terms_of_service

import com.rafalskrzypczyk.firestore.domain.models.TermsOfServiceDTO

data class TermsOfServiceState(
    val terms: TermsOfServiceDTO? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAccepted: Boolean = false
)
