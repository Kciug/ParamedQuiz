package com.rafalskrzypczyk.main_mode.domain.quiz

import com.rafalskrzypczyk.main_mode.domain.quiz_base.BaseQuizUseCases
import com.rafalskrzypczyk.score.domain.use_cases.IncreaseStreakByQuestionsUC
import javax.inject.Inject

data class MMQuizUseCases @Inject constructor(
    val base: BaseQuizUseCases,
    val getQuestionsForCategory: GetShuffledQuestionsForCategoryUC,
    val getUpdatedQuestions: GetUpdatedQuestionsUC,
    val updateStreak: IncreaseStreakByQuestionsUC
)
