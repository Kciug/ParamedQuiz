package com.rafalskrzypczyk.home_screen.domain

import com.rafalskrzypczyk.core.user_management.CheckIsUserLoggedInUC
import com.rafalskrzypczyk.firestore.domain.use_cases.GetQuestionsCountUC
import com.rafalskrzypczyk.home_screen.domain.use_cases.GetUserDataUC
import com.rafalskrzypczyk.score.domain.use_cases.GetStreakStateUC
import com.rafalskrzypczyk.score.domain.use_cases.GetUserScoreUC
import javax.inject.Inject

data class HomeScreenUseCases @Inject constructor(
    val getUserScore: GetUserScoreUC,
    val getUserData: GetUserDataUC,
    val getStreakState: GetStreakStateUC,
    val checkIsUserLoggedIn: CheckIsUserLoggedInUC,
    val checkDailyExerciseAvailability: CheckDailyExerciseAvailabilityUC,
    val getQuestionsCount: GetQuestionsCountUC
)
