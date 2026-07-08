package com.rafalskrzypczyk.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import com.rafalskrzypczyk.score.domain.ScoreRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import java.util.Date

/**
 * Zależności workera wstrzykiwane przez Hilt [EntryPoint] (zamiast @HiltWorker —
 * androidx.hilt:hilt-compiler 1.2.0 nie czyta metadanych Kotlin 2.1 przez kapt).
 * Interfejs celowo top-level, by agregacja Hilta w :app nie musiała rozwiązywać CoroutineWorker.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface ReminderWorkerEntryPoint {
    fun scoreRepository(): ScoreRepository
    fun notifier(): Notifier
    fun sharedPrefs(): SharedPreferencesApi
    fun reminderScheduler(): ReminderScheduler
}

/**
 * Ocenia warunki i wysyła jeden reminder wg pseudokodu spec §5.2
 * (Streak > Win-back > Daily; Revision dojdzie później). Po każdym uruchomieniu
 * przeplanowuje się na kolejny dzień.
 */
class ReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val deps = EntryPointAccessors.fromApplication(appContext, ReminderWorkerEntryPoint::class.java)
    private val decider = ReminderDecider()

    override suspend fun doWork(): Result {
        val sharedPrefs = deps.sharedPrefs()
        val reminderScheduler = deps.reminderScheduler()

        // Wyłączone w apce — nic nie rób i nie przeplanowuj (anulowanie realizuje scheduler).
        if (!sharedPrefs.isNotificationsEnabled()) return Result.success()

        // Zablokowane w systemie — spróbujemy ponownie następnego dnia.
        if (!NotificationPermission.areNotificationsEnabled(applicationContext)) {
            reminderScheduler.schedule()
            return Result.success()
        }

        val scoreResponse = deps.scoreRepository().getUserScore().first { it !is Response.Loading }
        if (scoreResponse is Response.Success) {
            val score = scoreResponse.data
            val decision = decider.decide(
                lastStreakUpdateDate = score.lastStreakUpdateDate,
                streak = score.streak,
                now = Date(),
                lastWinbackDaySent = sharedPrefs.getLastWinbackDaySent()
            )
            handleDecision(decision, sharedPrefs)
        }
        // Response.Error / brak danych → zachowawczo nic nie wysyłamy.

        reminderScheduler.schedule()
        return Result.success()
    }

    private fun handleDecision(decision: ReminderDecision, sharedPrefs: SharedPreferencesApi) {
        val notifier = deps.notifier()
        when (decision) {
            // None występuje tylko przy DONE dziś — resetujemy próg win-backu na nową nieaktywność.
            ReminderDecision.None -> sharedPrefs.setLastWinbackDaySent(0)

            ReminderDecision.Daily -> notifier.show(
                notificationId = NotificationIds.DAILY_REMINDER,
                title = applicationContext.getString(R.string.notification_daily_title),
                text = DailyReminderContent.random(),
                destination = NotificationDestination.HOME
            )

            is ReminderDecision.Streak -> notifier.show(
                notificationId = NotificationIds.STREAK_REMINDER,
                title = applicationContext.getString(R.string.notification_streak_title),
                text = StreakReminderContent.random(decision.streak),
                destination = NotificationDestination.HOME
            )

            is ReminderDecision.Winback -> {
                notifier.show(
                    notificationId = NotificationIds.WINBACK,
                    title = applicationContext.getString(R.string.notification_winback_title),
                    text = WinbackReminderContent.forDay(decision.day),
                    destination = NotificationDestination.HOME
                )
                sharedPrefs.setLastWinbackDaySent(decision.day)
            }
        }
    }
}
