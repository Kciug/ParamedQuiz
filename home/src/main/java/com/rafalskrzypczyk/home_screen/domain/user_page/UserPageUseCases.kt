package com.rafalskrzypczyk.home_screen.domain.user_page

import com.rafalskrzypczyk.core.user_management.GetUserUC
import com.rafalskrzypczyk.score.domain.use_cases.GetStreakStateUC
import com.rafalskrzypczyk.score.domain.use_cases.GetUserScoreUC
import javax.inject.Inject

data class UserPageUseCases @Inject constructor(
    val getUser: GetUserUC,
    val getUserScore: GetUserScoreUC,
    val getStreakState: GetStreakStateUC,
    val getOverallResult: GetOverallResultUC,
    val getMainModeResult: GetMainModeResultUC,
    val getSwipeModeResult: GetSwipeModeResultUC,
    val getTranslationModeResult: GetTranslationModeResultUC,
    val getQuestionsForMode: GetQuestionsForModeUC,
    val getCombinedQuestionsData: GetCombinedQuestionsDataUC,
    val getBestQuestions: GetBestQuestionsUC,
    val getWorstQuestions: GetWorstQuestionsUC,
)
