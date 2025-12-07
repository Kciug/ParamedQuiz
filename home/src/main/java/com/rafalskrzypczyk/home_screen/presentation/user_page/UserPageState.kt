package com.rafalskrzypczyk.home_screen.presentation.user_page

import androidx.compose.runtime.Immutable
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.home_screen.presentation.user_page.statistics.BestWorstQuestionsUIM
import com.rafalskrzypczyk.score.domain.StreakState

@Immutable
data class UserPageState (
    val isUserLoggedIn: Boolean = false, // Nowe pole
    val error: String? = null,
    val userName: String = "",
    val userEmail: String = "",
    val userScore: Int = 0,
    val userStreak: Int = 0,
    val userStreakState: StreakState = StreakState.MISSED,
    val overallResultAvailable: Boolean = false,
    val overallResult: Int = 0,
    val mainModeResultResponse: ResponseState = ResponseState.Idle,
    val swipeModeResultResponse: ResponseState = ResponseState.Idle,
    val mainModeResultAvailable: Boolean = false,
    val swipeModeResultAvailable: Boolean = false,
    val mainModeResult: Int = 0,
    val swipeModeResult: Int = 0,
    val bestWorstQuestions: BestWorstQuestionsUIM = BestWorstQuestionsUIM()
)