package com.rafalskrzypczyk.main_mode.domain.quiz_categories

import com.rafalskrzypczyk.core.user_management.CheckIsUserLoggedInUC
import com.rafalskrzypczyk.score.domain.GetUserScoreUC
import javax.inject.Inject


data class MMCategoriesUseCases @Inject constructor(
    val getAllCategories: GetAllCategoriesUC,
    val getUpdatedCategories: GetUpdatedCategoriesUC,
    val getUserScore: GetUserScoreUC,
    val checkIsUserLoggedIn: CheckIsUserLoggedInUC
)
