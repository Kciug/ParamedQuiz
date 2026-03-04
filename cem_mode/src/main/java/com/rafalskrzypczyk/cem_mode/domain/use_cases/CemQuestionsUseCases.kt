package com.rafalskrzypczyk.cem_mode.domain.use_cases

import com.rafalskrzypczyk.main_mode.domain.quiz_base.BaseQuizUseCases
import com.rafalskrzypczyk.score.domain.use_cases.IncreaseStreakByQuestionsUC
import javax.inject.Inject

data class CemQuestionsUseCases @Inject constructor(
    val base: BaseQuizUseCases,
    val getQuestionsForCategory: GetShuffledQuestionsForCemCategoryUC,
    val getUpdatedQuestions: GetUpdatedCemQuestionsUC,
    val updateStreak: IncreaseStreakByQuestionsUC
)