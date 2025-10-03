package com.rafalskrzypczyk.main_mode.presentation.daily_exercise

import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.main_mode.R
import com.rafalskrzypczyk.main_mode.domain.daily_exercise.DailyExerciseUseCases
import com.rafalskrzypczyk.main_mode.presentation.quiz_base.BaseQuizVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyExerciseVM @Inject constructor(
    private val useCases: DailyExerciseUseCases,
    private val resourceProvider: ResourceProvider
): BaseQuizVM(useCases = useCases.base) {
    companion object {
        const val DAILY_EXERCISE_QUESTIONS_AMOUNT = 3
    }

    init {
        viewModelScope.launch { loadQuestions() }
    }

    override suspend fun loadQuestions() {
        useCases.getQuestions().collectLatest { response ->
            when (response) {
                is Response.Success -> {
                    initializeQuiz(
                        questions = response.data.take(DAILY_EXERCISE_QUESTIONS_AMOUNT),
                        title = resourceProvider.getString(R.string.title_daily_exercise)
                    )
                    attachQuestionsListener()
                }
                is Response.Error -> { _state.update { it.copy(responseState = ResponseState.Error(response.error)) }}
                Response.Loading -> { _state.update { it.copy(responseState = ResponseState.Loading) }}
            }
        }
    }

    private fun attachQuestionsListener() {
        viewModelScope.launch {
            useCases.getUpdatedQuestions().collectLatest { newQuestions ->
                updateQuizData(newQuestions)
            }
        }
    }

    override fun displayNextQuestion() {
        super.displayNextQuestion()
        if (state.value.isQuizFinished) {
            useCases.updateStreak()
            useCases.updateLastDailyExerciseDate()
        }
    }
}