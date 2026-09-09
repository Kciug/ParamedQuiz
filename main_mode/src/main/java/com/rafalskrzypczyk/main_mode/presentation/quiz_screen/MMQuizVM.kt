package com.rafalskrzypczyk.main_mode.presentation.quiz_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.ads.QuizAdHandler
import com.rafalskrzypczyk.core.analytics.AnalyticsEvent
import com.rafalskrzypczyk.core.analytics.AnalyticsLogger
import com.rafalskrzypczyk.core.analytics.QuizSource
import com.rafalskrzypczyk.core.analytics.analyticsName
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.feedback.FeedbackEvent
import com.rafalskrzypczyk.core.feedback.FeedbackManager
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.main_mode.domain.quiz.MMQuizUseCases
import com.rafalskrzypczyk.main_mode.presentation.quiz_base.BaseQuizVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val GAME_MODE_NAME = "Main Mode"

@HiltViewModel
class MMQuizVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCases: MMQuizUseCases,
    adHandler: QuizAdHandler,
    feedbackManager: FeedbackManager,
    private val analyticsLogger: AnalyticsLogger
): BaseQuizVM(
    useCases = useCases.base,
    adHandler = adHandler,
    feedbackManager = feedbackManager,
    analyticsLogger = analyticsLogger,
    quizMode = QuizMode.MainMode,
    analyticsSource = QuizSource.CATEGORY,
    gameMode = GAME_MODE_NAME
) {
    private val categoryId: Long = savedStateHandle.get<Long>("categoryId") ?: -1
    private val categoryTitle: String = savedStateHandle.get<String>("categoryTitle") ?: ""

    init {
        // Wybór odblokowanej kategorii omija MMCategoriesVM (composable woła nawigację wprost),
        // więc jedyne miejsce, w którym da się go zobaczyć, to start quizu.
        analyticsLogger.log(
            AnalyticsEvent.CategorySelected(
                mode = QuizMode.MainMode.analyticsName(),
                categoryId = categoryId,
                locked = false,
            )
        )

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
        if (useCases.updateStreak()) {
            isStreakUpdatedInSession = true
            feedbackManager.perform(FeedbackEvent.STREAK_UP)
        }
    }

    private fun attachQuestionsListener() {
        viewModelScope.launch {
            useCases.getUpdatedQuestions().collectLatest { newQuestions ->
                updateQuizData(newQuestions)
            }
        }
    }
}