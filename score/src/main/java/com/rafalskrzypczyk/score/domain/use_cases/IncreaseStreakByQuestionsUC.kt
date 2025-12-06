package com.rafalskrzypczyk.score.domain.use_cases

import com.rafalskrzypczyk.score.domain.StreakManager
import javax.inject.Inject

class IncreaseStreakByQuestionsUC @Inject constructor(
    private val streakManager: StreakManager
){
    operator fun invoke(): Boolean {
        return streakManager.increaseStreakByQuestions()
    }
}