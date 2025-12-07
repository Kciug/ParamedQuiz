package com.rafalskrzypczyk.home_screen.presentation.user_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.home_screen.domain.models.QuizMode
import com.rafalskrzypczyk.home_screen.domain.models.SimpleQuestion
import com.rafalskrzypczyk.home_screen.domain.models.next
import com.rafalskrzypczyk.home_screen.domain.models.previous
import com.rafalskrzypczyk.home_screen.domain.user_page.UserPageUseCases
import com.rafalskrzypczyk.home_screen.presentation.user_page.statistics.BestWorstQuestionsUIM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserPageVM @Inject constructor(
    private val useCases: UserPageUseCases
) : ViewModel() {
    companion object {
        private const val QUESTIONS_TO_SHOW = 5
    }

    private val _state = MutableStateFlow(UserPageState())
    val state: StateFlow<UserPageState> = _state.asStateFlow()

    private var quizStatsMode = QuizMode.MainMode

    fun onEvent(event: UserPageUIEvents) {
        when(event) {
            UserPageUIEvents.RefreshUserData -> getUserData()
            UserPageUIEvents.OnNextMode -> onNextMode()
            UserPageUIEvents.OnPreviousMode -> onPreviousMode()
        }
    }

    private fun getUserData() {
        val user = useCases.getUser()
        if (user != null) {
            _state.update {
                it.copy(
                    isUserLoggedIn = true,
                    userName = user.name,
                    userEmail = user.email
                )
            }
        } else {
            _state.update { it.copy(isUserLoggedIn = false) }
        }
        
        getScoreData()
    }

    private fun getScoreData() {
        viewModelScope.launch {
            useCases.getUserScore().collect { score ->
                _state.update {
                    it.copy(
                        userScore = score.score,
                        userStreak = score.streak,
                        userStreakState = useCases.getStreakState(score.lastStreakUpdateDate)
                    )
                }
                getResultsData()
                getQuestionsData()
            }
        }
    }

    private fun getResultsData() {
        val overallResult = useCases.getOverallResult()
        _state.update {
            it.copy(
                overallResultAvailable = overallResult != null,
                overallResult = overallResult ?: 0
            )
        }

        viewModelScope.launch {
            useCases.getMainModeResult().collect { response ->
                when(response) {
                    is Response.Error -> {
                        _state.update { it.copy(mainModeResultResponse = ResponseState.Error(response.error)) }
                    }
                    Response.Loading -> {
                        _state.update { it.copy(mainModeResultResponse = ResponseState.Loading) }
                    }
                    is Response.Success -> {
                        val data = response.data
                        _state.update { it.copy(
                            mainModeResultResponse = ResponseState.Success,
                            mainModeResultAvailable = data != null,
                            mainModeResult = data ?: 0
                        ) }
                    }
                }
            }
        }

        viewModelScope.launch {
            useCases.getSwipeModeResult().collect { response ->
                when(response) {
                    is Response.Error -> {
                        _state.update { it.copy(swipeModeResultResponse = ResponseState.Error(response.error)) }
                    }
                    Response.Loading -> {
                        _state.update { it.copy(swipeModeResultResponse = ResponseState.Loading) }
                    }
                    is Response.Success -> {
                        val data = response.data
                        _state.update { it.copy(
                            swipeModeResultResponse = ResponseState.Success,
                            swipeModeResultAvailable = data != null,
                            swipeModeResult = data ?: 0
                        ) }
                    }
                }
            }
        }
    }

    private fun getQuestionsData() {
        _state.update {
            it.copy(
                bestWorstQuestions = BestWorstQuestionsUIM(
                    currentMode = quizStatsMode,
                    responseState = ResponseState.Idle,
                )
            )
        }

        viewModelScope.launch {
            useCases.getQuestionsForMode(quizStatsMode).collect { response ->
                when (response) {
                    is Response.Error -> _state.update { it.copy(
                        error = response.error
                    ) }
                    Response.Loading -> _state.update { it.copy(
                        bestWorstQuestions = it.bestWorstQuestions.copy(responseState = ResponseState.Loading)
                    ) }
                    is Response.Success -> getBestWorstQuestions(response.data)
                }
            }

        }
    }

    private fun getBestWorstQuestions(allQuestions: List<SimpleQuestion>) {
        viewModelScope.launch(Dispatchers.Default) {
            val combinedQuestionsData = useCases.getCombinedQuestionsData(allQuestions)
            val dataAvailableToShow = combinedQuestionsData.size >= QUESTIONS_TO_SHOW * 2
            
            val bestQuestions = useCases.getBestQuestions(combinedQuestionsData, QUESTIONS_TO_SHOW)
            val worstQuestions = useCases.getWorstQuestions(combinedQuestionsData, QUESTIONS_TO_SHOW)

            _state.update {
                it.copy(
                    bestWorstQuestions = it.bestWorstQuestions.copy(
                        responseState = ResponseState.Success,
                        dataAvailable = dataAvailableToShow,
                        bestQuestions = bestQuestions,
                        worstQuestions = worstQuestions
                    )
                )
            }
        }
    }

    private fun onNextMode() {
        quizStatsMode = quizStatsMode.next()
        getQuestionsData()
    }

    private fun onPreviousMode() {
        quizStatsMode = quizStatsMode.previous()
        getQuestionsData()
    }
}