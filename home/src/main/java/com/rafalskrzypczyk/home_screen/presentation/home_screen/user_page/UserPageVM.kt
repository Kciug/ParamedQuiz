package com.rafalskrzypczyk.home_screen.presentation.home_screen.user_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.home_screen.domain.UserPageUseCases
import com.rafalskrzypczyk.home_screen.domain.models.QuizMode
import com.rafalskrzypczyk.home_screen.domain.models.SimpleQuestion
import com.rafalskrzypczyk.home_screen.domain.models.next
import com.rafalskrzypczyk.home_screen.domain.models.previous
import dagger.hilt.android.lifecycle.HiltViewModel
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
        useCases.getUser()?.let { userData ->
            _state.update {
                it.copy(
                    userName = userData.name,
                    userEmail = userData.email
                )
            }
        }
        getScoreData()
        getResultsData()
        getQuestionsData()
    }

    private fun getScoreData() {
        viewModelScope.launch {
            useCases.getUserScore().collect { score ->
                _state.update { it.copy(userScore = score.score.toInt()) }
            }
        }
    }

    private fun getResultsData() {
        _state.update {
            it.copy(overallResult = useCases.getOverallResult())
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
                        _state.update { it.copy(
                            mainModeResultResponse = ResponseState.Success,
                            mainModeResult = response.data
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
                        _state.update { it.copy(
                            swipeModeResultResponse = ResponseState.Success,
                            swipeModeResult = response.data
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
        _state.update {
            it.copy(
                bestWorstQuestions = it.bestWorstQuestions.copy(
                    responseState = ResponseState.Success,
                    bestQuestions = useCases.getBestQuestions(allQuestions),
                    worstQuestions = useCases.getWorstQuestions(allQuestions)
                )
            )
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