package com.rafalskrzypczyk.main_mode.presentation.categories_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.main_mode.domain.quiz_categories.MMCategoriesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MMCategoriesVM @Inject constructor(
    private val useCases: MMCategoriesUseCases
): ViewModel() {
    private val _state = MutableStateFlow(MMCategoriesState())
    val state = _state.asStateFlow()

    fun onEvent(event: MMCategoriesUIEvents) {
        when(event) {
            MMCategoriesUIEvents.GetData -> loadData()
            is MMCategoriesUIEvents.OnUnlockCategory -> onUnlockCategory(event.categoryId)
            MMCategoriesUIEvents.DiscardUnlockCategory -> discardUnlockCategory()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            useCases.getUserScore().collectLatest { userScore ->
                _state.update {
                    it.copy(
                        userScore = userScore.score,
                        userStreak = userScore.streak,
                        userStreakState = useCases.getStreakState(userScore.lastStreakUpdateDate),
                        isUserLoggedIn = useCases.checkIsUserLoggedIn()
                    )
                }
            }
        }

        viewModelScope.launch {
            useCases.getAllCategories().collectLatest { response ->
                when(response) {
                    is Response.Error -> _state.update { it.copy(responseState = ResponseState.Error(response.error)) }
                    Response.Loading -> _state.update { it.copy(responseState = ResponseState.Loading) }
                    is Response.Success -> {
                        attachCategoriesListener()
                        _state.update { state ->
                            state.copy(
                                responseState = ResponseState.Success,
                                categories = response.data.map { it.toUIM() })
                        }
                    }
                }
            }
        }
    }

    private fun attachCategoriesListener() {
        viewModelScope.launch {
            useCases.getUpdatedCategories().collectLatest { data ->
                _state.update { state ->
                    state.copy(categories = data.map { it.toUIM() } )
                }
            }
        }
    }

    private fun onUnlockCategory(categoryId: Long) {
        _state.update { it.copy(unlockCategory = true) }
    }

    private fun discardUnlockCategory() {
        _state.update { it.copy(unlockCategory = false) }
    }
}