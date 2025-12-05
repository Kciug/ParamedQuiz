package com.rafalskrzypczyk.home_screen.presentation.user_page.statistics

import androidx.compose.runtime.Immutable
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.home_screen.domain.models.QuestionWithStats
import com.rafalskrzypczyk.home_screen.domain.models.QuizMode

@Immutable
data class BestWorstQuestionsUIM(
    val currentMode: QuizMode = QuizMode.MainMode,
    val responseState: ResponseState = ResponseState.Idle,
    val dataAvailable: Boolean = false,
    val bestQuestions: List<QuestionWithStats> = emptyList(),
    val worstQuestions: List<QuestionWithStats> = emptyList()
)
