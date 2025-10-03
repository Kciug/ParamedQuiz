package com.rafalskrzypczyk.main_mode.domain.daily_exercise

import com.rafalskrzypczyk.main_mode.domain.quiz.GetUpdatedQuestionsUC
import com.rafalskrzypczyk.main_mode.domain.quiz_base.BaseQuizUseCases
import com.rafalskrzypczyk.score.domain.use_cases.IncreaseStreakInstantlyUC
import com.rafalskrzypczyk.score.domain.use_cases.UpdateLastDailyExerciseDateUC
import javax.inject.Inject

data class DailyExerciseUseCases @Inject constructor(
    val base: BaseQuizUseCases,
    val getQuestions: GetAllQuestionsShuffledUC,
    val getUpdatedQuestions: GetUpdatedQuestionsUC,
    val updateStreak: IncreaseStreakInstantlyUC,
    val updateLastDailyExerciseDate: UpdateLastDailyExerciseDateUC
)
