package com.rafalskrzypczyk.swipe_mode.domain

import com.rafalskrzypczyk.score.domain.use_cases.UpdateScoreWithQuestionUC
import javax.inject.Inject

data class SwipeModeUseCases @Inject constructor (
    val getShuffledSwipeQuestions: GetShuffledSwipeQuestionsUC,
    val updateScore: UpdateScoreWithQuestionUC
)