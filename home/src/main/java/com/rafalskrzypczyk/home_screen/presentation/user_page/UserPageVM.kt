package com.rafalskrzypczyk.home_screen.presentation.user_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.home_screen.domain.models.SimpleQuestion
import com.rafalskrzypczyk.core.utils.next
import com.rafalskrzypczyk.core.utils.previous
import com.rafalskrzypczyk.home_screen.domain.user_page.UserPageUseCases
import com.rafalskrzypczyk.home_screen.presentation.user_page.statistics.BestWorstQuestionsUIM
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import com.rafalskrzypczyk.core.utils.toDateOnly
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class UserPageVM @Inject constructor(
    private val useCases: UserPageUseCases,
    private val premiumStatusProvider: PremiumStatusProvider
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

        viewModelScope.launch {
            premiumStatusProvider.ownedProductIds.collectLatest { ownedIds ->
                val isPremium = ownedIds.contains(BillingIds.ID_FULL_PACKAGE)
                _state.update { it.copy(isPremium = isPremium) }
            }
        }
        
        getScoreData()
    }

    private fun getScoreData() {
        viewModelScope.launch {
            useCases.getUserScore().collect { score ->
                val totalCorrect = score.seenQuestions.sumOf { it.timesCorrect }.toInt()
                val totalIncorrect = score.seenQuestions.sumOf { it.timesIncorrect }.toInt()
                val totalUnique = score.seenQuestions.size
                val totalIdeal = score.seenQuestions.count { it.timesCorrect > 0 && it.timesIncorrect == 0L }

                _state.update {
                    it.copy(
                        userScore = score.score,
                        userStreak = score.streak,
                        userStreakState = useCases.getStreakState(score.lastStreakUpdateDate),
                        weeklyStreak = calculateWeeklyStreak(score.streak, score.lastStreakUpdateDate),
                        totalCorrect = totalCorrect,
                        totalIncorrect = totalIncorrect,
                        totalUnique = totalUnique,
                        totalIdeal = totalIdeal
                    )
                }
                getResultsData()
                getQuestionsData()
            }
        }
    }

    private fun calculateWeeklyStreak(streak: Int, lastUpdate: Date?): List<Boolean> {
        if (lastUpdate == null || streak == 0) return List(7) { false }

        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        
        val lastUpdateDate = lastUpdate.toDateOnly()

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val mondayThisWeek = calendar.time.toDateOnly()

        if (lastUpdateDate < mondayThisWeek) return List(7) { false }

        val weeklyStreak = MutableList(7) { false }
        
        calendar.time = lastUpdateDate
        val lastUpdateDayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
        
        for (i in 0..6) {
            val daysAgo = lastUpdateDayOfWeek - i
            if (daysAgo >= 0 && streak > daysAgo) {
                weeklyStreak[i] = true
            }
        }
        
        return weeklyStreak
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
                            mainModeResult = data?.score ?: 0,
                            mainModeCorrect = data?.correctAnswers ?: 0,
                            mainModeTotal = data?.totalAnswers ?: 0
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
                            swipeModeResult = data?.score ?: 0,
                            swipeModeCorrect = data?.correctAnswers ?: 0,
                            swipeModeTotal = data?.totalAnswers ?: 0
                        ) }
                    }
                }
            }
        }

        viewModelScope.launch {
            useCases.getTranslationModeResult().collect { response ->
                when(response) {
                    is Response.Error -> {
                        _state.update { it.copy(translationModeResultResponse = ResponseState.Error(response.error)) }
                    }
                    Response.Loading -> {
                        _state.update { it.copy(translationModeResultResponse = ResponseState.Loading) }
                    }
                    is Response.Success -> {
                        val data = response.data
                        _state.update { it.copy(
                            translationModeResultResponse = ResponseState.Success,
                            translationModeResultAvailable = data != null,
                            translationModeResult = data?.score ?: 0,
                            translationModeCorrect = data?.correctAnswers ?: 0,
                            translationModeTotal = data?.totalAnswers ?: 0
                        ) }
                    }
                }
            }
        }

        viewModelScope.launch {
            useCases.getCemModeResult().collect { response ->
                when(response) {
                    is Response.Error -> {
                        _state.update { it.copy(cemModeResultResponse = ResponseState.Error(response.error)) }
                    }
                    Response.Loading -> {
                        _state.update { it.copy(cemModeResultResponse = ResponseState.Loading) }
                    }
                    is Response.Success -> {
                        val data = response.data
                        _state.update { it.copy(
                            cemModeResultResponse = ResponseState.Success,
                            cemModeResultAvailable = data != null,
                            cemModeResult = data?.score ?: 0,
                            cemModeCorrect = data?.correctAnswers ?: 0,
                            cemModeTotal = data?.totalAnswers ?: 0
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