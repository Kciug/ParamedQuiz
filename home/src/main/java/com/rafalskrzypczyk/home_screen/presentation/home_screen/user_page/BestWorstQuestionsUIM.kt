package com.rafalskrzypczyk.home_screen.presentation.home_screen.user_page

import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.home_screen.domain.models.QuestionWithStats
import com.rafalskrzypczyk.home_screen.domain.models.QuizMode

data class BestWorstQuestionsUIM(
    val currentMode: QuizMode = QuizMode.MainMode,
    val responseState: ResponseState = ResponseState.Idle,
    val bestQuestions: List<QuestionWithStats> = emptyList(),
    val worstQuestions: List<QuestionWithStats> = emptyList()
)
