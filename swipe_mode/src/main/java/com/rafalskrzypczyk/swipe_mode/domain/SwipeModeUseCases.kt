package com.rafalskrzypczyk.swipe_mode.domain

import javax.inject.Inject

data class SwipeModeUseCases @Inject constructor (
    val getShuffledSwipeQuestions: GetShuffledSwipeQuestionsUC
)