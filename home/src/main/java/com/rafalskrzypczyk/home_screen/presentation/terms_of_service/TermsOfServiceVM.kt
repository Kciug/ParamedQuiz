package com.rafalskrzypczyk.home_screen.presentation.terms_of_service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.api_response.Response
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
            useCases.getTermsOfServiceUC().collect { response ->
                when (response) {
                    is Response.Loading -> {
                        _state.value = _state.value.copy(isLoading = true, error = null)
                    }
                    is Response.Success -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            terms = response.data,
                            error = null
                        )
                    }
                    is Response.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = response.error
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
