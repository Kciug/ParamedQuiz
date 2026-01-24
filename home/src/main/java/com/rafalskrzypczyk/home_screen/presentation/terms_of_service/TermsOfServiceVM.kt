package com.rafalskrzypczyk.home_screen.presentation.terms_of_service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.firestore.domain.models.TermsOfServiceStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TermsOfServiceVM @Inject constructor(
    private val useCases: TermsOfServiceUseCases
) : ViewModel() {
    private val _state = MutableStateFlow(TermsOfServiceState())
    val state = _state.asStateFlow()

    fun onEvent(event: TermsOfServiceUIEvents) {
        when (event) {
            TermsOfServiceUIEvents.LoadTerms -> loadTerms()
            TermsOfServiceUIEvents.AcceptTerms -> acceptTerms()
        }
    }

    private fun loadTerms() {
        viewModelScope.launch {
            useCases.getTermsOfServiceUC(forceCheck = true).collect { status ->
                when (status) {
                    TermsOfServiceStatus.Loading -> {
                        _state.value = _state.value.copy(isLoading = true, error = null)
                    }
                    TermsOfServiceStatus.Accepted -> {
                        _state.value = _state.value.copy(isLoading = false, isAccepted = true)
                    }
                    is TermsOfServiceStatus.NeedsAcceptance -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            terms = status.terms,
                            isAccepted = false,
                            error = null
                        )
                    }
                    is TermsOfServiceStatus.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = status.message
                        )
                    }
                }
            }
        }
    }

    private fun acceptTerms() {
        _state.value.terms?.let {
            useCases.acceptTermsOfServiceUC(it.version)
            _state.value = _state.value.copy(isAccepted = true)
        }
    }
}
