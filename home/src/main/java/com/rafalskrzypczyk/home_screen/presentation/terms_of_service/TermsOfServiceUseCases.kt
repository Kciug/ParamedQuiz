package com.rafalskrzypczyk.home_screen.presentation.terms_of_service

import com.rafalskrzypczyk.firestore.domain.use_cases.AcceptTermsOfServiceUC
import com.rafalskrzypczyk.firestore.domain.use_cases.GetTermsOfServiceUC
import javax.inject.Inject

data class TermsOfServiceUseCases @Inject constructor(
    val getTermsOfServiceUC: GetTermsOfServiceUC,
    val acceptTermsOfServiceUC: AcceptTermsOfServiceUC
)
