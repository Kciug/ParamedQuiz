package com.rafalskrzypczyk.paramedquiz.dev

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.billing.domain.BillingRepository
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import com.rafalskrzypczyk.notifications.NotificationDestination
import com.rafalskrzypczyk.notifications.Notifier
import com.rafalskrzypczyk.notifications.ReminderScheduler
import com.rafalskrzypczyk.score.domain.QuestionAnnotation
import com.rafalskrzypczyk.score.domain.ScoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class DevVM @Inject constructor(
    private val sharedPreferences: SharedPreferencesApi,
    private val billingRepository: BillingRepository,
    private val notifier: Notifier,
    private val reminderScheduler: ReminderScheduler,
    private val scoreManager: ScoreManager
): ViewModel() {

    fun onEvent(event: DevOptionsUIEvents) {
        when(event) {
            DevOptionsUIEvents.ResetOnboarding -> resetOnboarding()
            DevOptionsUIEvents.ResetModularOnboarding -> resetModularOnboarding()
            DevOptionsUIEvents.ClearTermsAcceptance -> clearTerms()
            DevOptionsUIEvents.ResetRatingStats -> resetRatingStats()
            DevOptionsUIEvents.TriggerRatingPrompt -> triggerRatingPrompt()
            DevOptionsUIEvents.ResetNews -> resetNews()
            DevOptionsUIEvents.ResetPurchases -> resetPurchases()
            DevOptionsUIEvents.SendTestNotification -> sendTestNotification()
            DevOptionsUIEvents.TriggerNotificationConsent -> triggerNotificationConsent()
            DevOptionsUIEvents.TriggerReminderNow -> reminderScheduler.debugRunNow()
            DevOptionsUIEvents.SimStreakPending -> simulateStreak(daysAgo = 1, streak = 5)
            DevOptionsUIEvents.SimInactive7 -> simulateStreak(daysAgo = 7, streak = 5)
            DevOptionsUIEvents.SimInactive14 -> simulateStreak(daysAgo = 14, streak = 5)
            DevOptionsUIEvents.SimWeakQuestions -> simulateWeakQuestions()
        }
    }

    private fun simulateStreak(daysAgo: Int, streak: Int) {
        val date = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -daysAgo) }.time
        scoreManager.updateScore(
            scoreManager.getScore().copy(streak = streak, lastStreakUpdateDate = date)
        )
        scoreManager.forceSync()
        sharedPreferences.setLastWinbackDaySent(0)
    }

    private fun simulateWeakQuestions() {
        val threeDaysAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -3) }.time
        val weak = listOf(
            QuestionAnnotation(questionId = -1L, timesSeen = 4, timesCorrect = 1),
            QuestionAnnotation(questionId = -2L, timesSeen = 4, timesCorrect = 1),
            QuestionAnnotation(questionId = -3L, timesSeen = 4, timesCorrect = 1)
        )
        scoreManager.updateScore(
            scoreManager.getScore().copy(lastStreakUpdateDate = threeDaysAgo, seenQuestions = weak)
        )
        scoreManager.forceSync()
        sharedPreferences.setLastRevisionReminderDate(0L)
    }

    private fun triggerNotificationConsent() {
        sharedPreferences.resetNotificationPromptCount()
        sharedPreferences.setLastNotificationPromptDate(0L)
        sharedPreferences.setNotificationPromptDisabled(false)
        sharedPreferences.setNotificationsEnabled(false)
        if (sharedPreferences.getCompletedQuizzesCount() < 1) {
            sharedPreferences.incrementCompletedQuizzesCount()
        }
    }

    private fun sendTestNotification() {
        notifier.show(
            notificationId = TEST_NOTIFICATION_ID,
            title = "Testowe powiadomienie",
            text = "To jest testowe powiadomienie. Tapnij, aby otworzyć ekran główny.",
            destination = NotificationDestination.HOME
        )
    }

    private fun resetPurchases() {
        viewModelScope.launch {
            val purchases = billingRepository.purchases.first()
            purchases.forEach { purchase ->
                billingRepository.consumePurchase(purchase.purchaseToken)
            }
        }
    }

    private fun resetNews() {
        sharedPreferences.clearSeenNewsIds()
    }

    private fun resetOnboarding() {
        sharedPreferences.setOnboardingStatus(false)
    }

    private fun resetModularOnboarding() {
        sharedPreferences.resetModularOnboarding()
    }

    private fun clearTerms() {
        sharedPreferences.setAcceptedTermsVersion(-1)
    }

    private fun resetRatingStats() {
        sharedPreferences.setAppRated(false)
        sharedPreferences.setLastRatingPromptDate(0L)
        sharedPreferences.setInstallDate(System.currentTimeMillis())
        sharedPreferences.resetCompletedQuizzesCount()
    }

    private fun triggerRatingPrompt() {
        val sevenDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
        sharedPreferences.setInstallDate(sevenDaysAgo)
        sharedPreferences.setAppRated(false)
        sharedPreferences.setLastRatingPromptDate(0L)
        
        sharedPreferences.resetCompletedQuizzesCount()
        repeat(5) {
            sharedPreferences.incrementCompletedQuizzesCount()
        }
    }

    companion object {
        private const val TEST_NOTIFICATION_ID = 9999
    }
}
