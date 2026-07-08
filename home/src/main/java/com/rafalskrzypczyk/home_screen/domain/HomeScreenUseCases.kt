package com.rafalskrzypczyk.home_screen.domain

import com.rafalskrzypczyk.core.domain.use_cases.CheckAppRatingEligibilityUC
import com.rafalskrzypczyk.core.domain.use_cases.CheckNotificationConsentEligibilityUC
import com.rafalskrzypczyk.core.domain.use_cases.DisableNotificationPromptUC
import com.rafalskrzypczyk.core.domain.use_cases.DisableRatingPromptUC
import com.rafalskrzypczyk.core.domain.use_cases.DismissAppRatingUC
import com.rafalskrzypczyk.core.domain.use_cases.MarkNotificationPromptShownUC
import com.rafalskrzypczyk.core.domain.use_cases.SetAppRatedUC
import com.rafalskrzypczyk.core.domain.use_cases.SetNotificationsEnabledUC
import com.rafalskrzypczyk.core.user_management.CheckIsUserLoggedInUC
import com.rafalskrzypczyk.firestore.domain.use_cases.GetQuestionsCountUC
import com.rafalskrzypczyk.firestore.domain.use_cases.SaveFeedbackUC
import com.rafalskrzypczyk.home_screen.domain.use_cases.GetNewsBannersUC
import com.rafalskrzypczyk.home_screen.domain.use_cases.GetUserDataUC
import com.rafalskrzypczyk.home_screen.domain.use_cases.MarkNewsAsSeenUC
import com.rafalskrzypczyk.home_screen.domain.use_cases.ResetSeenNewsUC
import com.rafalskrzypczyk.score.domain.use_cases.GetStreakStateUC
import com.rafalskrzypczyk.score.domain.use_cases.GetUserScoreUC
import javax.inject.Inject

data class HomeScreenUseCases @Inject constructor(
    val getUserScore: GetUserScoreUC,
    val getUserData: GetUserDataUC,
    val getStreakState: GetStreakStateUC,
    val checkIsUserLoggedIn: CheckIsUserLoggedInUC,
    val checkDailyExerciseAvailability: CheckDailyExerciseAvailabilityUC,
    val getQuestionsCount: GetQuestionsCountUC,
    val checkAppRatingEligibility: CheckAppRatingEligibilityUC,
    val setAppRated: SetAppRatedUC,
    val dismissAppRating: DismissAppRatingUC,
    val disableRatingPrompt: DisableRatingPromptUC,
    val saveFeedback: SaveFeedbackUC,
    val getNewsBanners: GetNewsBannersUC,
    val markNewsAsSeen: MarkNewsAsSeenUC,
    val resetSeenNews: ResetSeenNewsUC,
    val checkNotificationConsentEligibility: CheckNotificationConsentEligibilityUC,
    val markNotificationPromptShown: MarkNotificationPromptShownUC,
    val setNotificationsEnabled: SetNotificationsEnabledUC,
    val disableNotificationPrompt: DisableNotificationPromptUC
)
