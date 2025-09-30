package com.rafalskrzypczyk.main_mode.presentation.quiz_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.main_mode.domain.quiz.MMQuizUseCases
import com.rafalskrzypczyk.main_mode.presentation.quiz_base.BaseQuizVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MMQuizVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: MMQuizUseCases
): BaseQuizVM(useCases = useCases.base) {
    private val categoryId: Long = savedStateHandle.get<Long>("categoryId") ?: -1
    private val categoryTitle: String = savedStateHandle.get<String>("categoryTitle") ?: ""

    init {
        viewModelScope.launch {
            loadQuestions()
        }
    }

    override suspend fun loadQuestions() {
        useCases.getQuestionsForCategory(categoryId).collectLatest { response ->
            when (response) {
                is Response.Success -> {
                    initializeQuiz(response.data, categoryTitle)
                    attachQuestionsListener()
                }
                is Response.Error -> { _state.update { it.copy(responseState = ResponseState.Error(response.error)) }}
                Response.Loading -> { _state.update { it.copy(responseState = ResponseState.Loading) }}
            }
        }
    }

    override fun submitAnswer() {
        super.submitAnswer()
        useCases.updateStreak()
    }

    private fun attachQuestionsListener() {
        viewModelScope.launch {
            useCases.getUpdatedQuestions().collectLatest { newQuestions ->
                updateQuizData(newQuestions)
            }
        }
    }
}