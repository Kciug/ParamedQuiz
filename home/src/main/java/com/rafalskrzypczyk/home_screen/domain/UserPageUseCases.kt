package com.rafalskrzypczyk.home_screen.domain

import com.rafalskrzypczyk.core.user_management.GetUserUC
import com.rafalskrzypczyk.score.domain.GetUserScoreUC
import javax.inject.Inject

data class UserPageUseCases @Inject constructor(
    val getUser: GetUserUC,
    val getUserScore: GetUserScoreUC,
    val getOverallResult: GetOverallResultUC,
    val getMainModeResult: GetMainModeResultUC,
    val getSwipeModeResult: GetSwipeModeResultUC,
    val getQuestionsForMode: GetQuestionsForModeUC,
    val getBestQuestions: GetBestQuestionsUC,
    val getWorstQuestions: GetWorstQuestionsUC,
)
