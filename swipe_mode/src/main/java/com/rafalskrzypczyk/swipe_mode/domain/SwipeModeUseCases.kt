package com.rafalskrzypczyk.swipe_mode.domain

import com.rafalskrzypczyk.core.domain.use_cases.IncrementCompletedQuizzesUC
import com.rafalskrzypczyk.firestore.domain.use_cases.GetQuestionsCountUC
import com.rafalskrzypczyk.firestore.domain.use_cases.ReportIssueUC
import com.rafalskrzypczyk.score.domain.use_cases.GetStreakUC
import com.rafalskrzypczyk.score.domain.use_cases.GetUserScoreUC
import com.rafalskrzypczyk.score.domain.use_cases.IncreaseStreakByQuestionsUC
import com.rafalskrzypczyk.score.domain.use_cases.UpdateScoreWithQuestionUC
import javax.inject.Inject

data class SwipeModeUseCases @Inject constructor (
    val getShuffledSwipeQuestions: GetShuffledSwipeQuestionsUC,
    val getShuffledSwipeTrialQuestions: GetShuffledSwipeTrialQuestionsUC,
    val getUpdatedSwipeQuestions: GetUpdatedSwipeQuestionsUC,
    val getUpdatedSwipeTrialQuestions: GetUpdatedSwipeTrialQuestionsUC,
    val getUserScore: GetUserScoreUC,
    val updateScore: UpdateScoreWithQuestionUC,
    val updateStreak: IncreaseStreakByQuestionsUC,
    val getStreak: GetStreakUC,
    val reportIssue: ReportIssueUC,
    val getQuestionsCount: GetQuestionsCountUC,
    val incrementCompletedQuizzes: IncrementCompletedQuizzesUC
)
