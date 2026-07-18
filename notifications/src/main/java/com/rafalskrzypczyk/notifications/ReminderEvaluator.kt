package com.rafalskrzypczyk.notifications

import android.content.Context
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import com.rafalskrzypczyk.notifications.config.NotificationConfigRepository
import com.rafalskrzypczyk.score.domain.ScoreRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Ocenia warunki i wysyła co najwyżej jeden reminder wg pseudokodu spec §5.2
 * (Streak > Win-back > Revision > Daily; DONE dziś → nic). Czysta ocena + wysyłka — BEZ planowania.
 * Re-arm (kolejny alarm) realizuje [ReminderReceiver].
 *
 * Wydzielone z dawnego ReminderWorker, by tę samą logikę odpalał odbiornik AlarmManagera
 * oraz dev-akcja „Uruchom przypomnienie teraz".
 */
@Singleton
class ReminderEvaluator @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val scoreRepository: ScoreRepository,
    private val notifier: Notifier,
    private val sharedPrefs: SharedPreferencesApi,
    private val configRepository: NotificationConfigRepository,
) {
    private val decider = ReminderDecider()

    suspend fun evaluateAndNotify() {
        // Wyłączone w apce — nic nie rób.
        if (!sharedPrefs.isNotificationsEnabled()) return

        // Kategoria „Przypomnienia" wyłączona.
        if (!sharedPrefs.isRemindersEnabled()) return

        // Zablokowane w systemie.
        if (!NotificationPermission.areNotificationsEnabled(context)) return

        // Odświeżenie configu z Firestore (time-boxed do raz/7 dni); ciche na błędach.
        configRepository.refresh()

        val scoreResponse = scoreRepository.getUserScore().first { it !is Response.Loading }
        // Response.Error / brak danych → zachowawczo nic nie wysyłamy.
        if (scoreResponse is Response.Success) {
            val score = scoreResponse.data
            val weakCount = score.seenQuestions.count {
                it.timesSeen > 0 && it.timesCorrect.toDouble() / it.timesSeen < 0.5
            }
            val decision = decider.decide(
                lastStreakUpdateDate = score.lastStreakUpdateDate,
                streak = score.streak,
                now = Date(),
                lastWinbackDaySent = sharedPrefs.getLastWinbackDaySent(),
                weakQuestionsCount = weakCount,
                lastRevisionReminderDate = sharedPrefs.getLastRevisionReminderDate(),
                streakMinValue = configRepository.streakMinValue(),
                winbackDays = configRepository.winbackDays(),
                revisionIntervalDays = configRepository.revisionIntervalDays()
            )
            handleDecision(decision)
        }
    }

    private fun handleDecision(decision: ReminderDecision) {
        when (decision) {
            // None występuje tylko przy DONE dziś — resetujemy próg win-backu na nową nieaktywność.
            ReminderDecision.None -> sharedPrefs.setLastWinbackDaySent(0)

            ReminderDecision.Daily -> notifier.show(
                notificationId = NotificationIds.DAILY_REMINDER,
                title = context.getString(R.string.notification_daily_title),
                text = configRepository.dailyText(),
                destination = NotificationDestination.HOME
            )

            is ReminderDecision.Streak -> notifier.show(
                notificationId = NotificationIds.STREAK_REMINDER,
                title = context.getString(R.string.notification_streak_title),
                text = configRepository.streakText(decision.streak),
                destination = NotificationDestination.HOME
            )

            is ReminderDecision.Winback -> {
                notifier.show(
                    notificationId = NotificationIds.WINBACK,
                    title = context.getString(R.string.notification_winback_title),
                    text = configRepository.winbackText(decision.day),
                    destination = NotificationDestination.HOME
                )
                sharedPrefs.setLastWinbackDaySent(decision.day)
            }

            ReminderDecision.Revision -> {
                notifier.show(
                    notificationId = NotificationIds.REVISION,
                    title = context.getString(R.string.notification_revision_title),
                    text = configRepository.revisionText(),
                    destination = NotificationDestination.REVISIONS
                )
                sharedPrefs.setLastRevisionReminderDate(System.currentTimeMillis())
            }
        }
    }
}
