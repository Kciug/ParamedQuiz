package com.rafalskrzypczyk.main_mode.presentation.daily_exercise

import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.ads.QuizAdHandler
import com.rafalskrzypczyk.core.analytics.AnalyticsEvent
import com.rafalskrzypczyk.core.analytics.AnalyticsLogger
import com.rafalskrzypczyk.core.analytics.QuizType
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.domain.config.GameplayConfigProvider
import com.rafalskrzypczyk.core.feedback.FeedbackEvent
import com.rafalskrzypczyk.core.feedback.FeedbackManager
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.main_mode.R
import com.rafalskrzypczyk.main_mode.domain.daily_exercise.DailyExerciseUseCases
import com.rafalskrzypczyk.main_mode.presentation.quiz_base.BaseQuizVM
import com.rafalskrzypczyk.score.domain.ScoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val GAME_MODE_NAME = "Daily Exercise"

@HiltViewModel
class DailyExerciseVM @Inject constructor(
    private val useCases: DailyExerciseUseCases,
    private val resourceProvider: ResourceProvider,
    private val scoreManager: ScoreManager,
    private val gameplayConfig: GameplayConfigProvider,
    adHandler: QuizAdHandler,
    feedbackManager: FeedbackManager,
    private val analyticsLogger: AnalyticsLogger,
): BaseQuizVM(
    useCases = useCases.base,
    adHandler = adHandler,
    feedbackManager = feedbackManager,
    analyticsLogger = analyticsLogger,
    quizMode = QuizMode.MainMode,
    quizType = QuizType.DAILY_QUEST,
    analyticsCategoryId = null,
    gameMode = GAME_MODE_NAME
) {
    private var hasLoggedDailyQuest = false

    init {
        viewModelScope.launch { loadQuestions() }
    }

    override suspend fun loadQuestions() {
        useCases.getQuestions().collectLatest { response ->
            when (response) {
                is Response.Success -> {
                    initializeQuiz(
                        questions = response.data.take(gameplayConfig.dailyExerciseQuestionsAmount()),
                        title = resourceProvider.getString(R.string.title_daily_exercise)
                    )
                    _state.update { it.copy(isDailyExercise = true) } 
                    
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

    override fun finishQuiz() {
        if (useCases.updateStreak()) {
            isStreakUpdatedInSession = true
            feedbackManager.perform(FeedbackEvent.STREAK_UP)
        }
        useCases.updateLastDailyExerciseDate()
        scoreManager.forceSync()
        logDailyQuestCompletedOnce()
        super.finishQuiz()
    }

    /**
     * Tutaj, a nie w quiz_complete: to jest moment, w ktorym dzien jest zuzyty
     * ([DailyExerciseUseCases.updateLastDailyExerciseDate]). quiz_complete zapada takze przy
     * wyjsciu przed pierwsza odpowiedzia, kiedy zadanie dnia zostaje dostepne.
     *
     * Bramka jest konieczna: finishQuiz() nie ma wlasnej, a doklada sie do niego zarowno
     * setFinishedState(), jak i zamkniecie reklamy.
     */
    private fun logDailyQuestCompletedOnce() {
        if (hasLoggedDailyQuest) return
        hasLoggedDailyQuest = true
        // Po updateStreak(), zeby streak_count zgadzal sie z tym, co widzi uzytkownik na wyniku.
        analyticsLogger.log(AnalyticsEvent.DailyQuestCompleted(scoreManager.getScore().streak))
    }
}