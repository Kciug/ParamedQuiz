package com.rafalskrzypczyk.home_screen.presentation.home_screen.user_page

import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.score.domain.StreakState

data class UserPageState (
    val error: String? = null,
    val userName: String = "",
    val userEmail: String = "",
    val userScore: Int = 0,
    val userStreak: Int = 0,
    val userStreakState: StreakState = StreakState.MISSED,
    val overallResult: Int = 0,
    val mainModeResultResponse: ResponseState = ResponseState.Idle,
    val swipeModeResultResponse: ResponseState = ResponseState.Idle,
    val mainModeResult: Int = 0,
    val swipeModeResult: Int = 0,
    val bestWorstQuestions: BestWorstQuestionsUIM = BestWorstQuestionsUIM()
)