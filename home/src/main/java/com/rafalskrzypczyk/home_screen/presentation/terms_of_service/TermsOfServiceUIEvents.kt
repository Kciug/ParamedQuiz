package com.rafalskrzypczyk.home_screen.presentation.terms_of_service

sealed interface TermsOfServiceUIEvents {
    data object LoadTerms : TermsOfServiceUIEvents
    data object AcceptTerms : TermsOfServiceUIEvents
}
