package com.rafalskrzypczyk.swipe_mode.domain

import com.rafalskrzypczyk.score.domain.use_cases.GetStreakUC
import com.rafalskrzypczyk.score.domain.use_cases.GetUserScoreUC
import com.rafalskrzypczyk.score.domain.use_cases.IncreaseStreakByQuestionsUC
import com.rafalskrzypczyk.score.domain.use_cases.UpdateScoreWithQuestionUC
import javax.inject.Inject

data class SwipeModeUseCases @Inject constructor (
    val getShuffledSwipeQuestions: GetShuffledSwipeQuestionsUC,
    val getUserScore: GetUserScoreUC,
    val updateScore: UpdateScoreWithQuestionUC,
    val updateStreak: IncreaseStreakByQuestionsUC,
    val getStreak: GetStreakUC
)