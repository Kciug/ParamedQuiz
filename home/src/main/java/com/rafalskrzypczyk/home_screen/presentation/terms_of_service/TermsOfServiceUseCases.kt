package com.rafalskrzypczyk.home_screen.presentation.terms_of_service

import com.rafalskrzypczyk.firestore.domain.use_cases.AcceptTermsOfServiceUC
import com.rafalskrzypczyk.firestore.domain.use_cases.GetTermsOfServiceUC
import com.rafalskrzypczyk.firestore.domain.use_cases.ListenTermsOfServiceUpdatesUC
import javax.inject.Inject

data class TermsOfServiceUseCases @Inject constructor(
    val getTermsOfServiceUC: GetTermsOfServiceUC,
    val listenTermsOfServiceUpdatesUC: ListenTermsOfServiceUpdatesUC,
    val acceptTermsOfServiceUC: AcceptTermsOfServiceUC
)
