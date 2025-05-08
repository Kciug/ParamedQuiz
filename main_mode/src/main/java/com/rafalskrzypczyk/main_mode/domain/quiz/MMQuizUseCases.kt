package com.rafalskrzypczyk.main_mode.domain.quiz

import javax.inject.Inject

data class MMQuizUseCases @Inject constructor(
    val getQuestionsForCategory: GetShuffledQuestionsForCategoryUC,
)
