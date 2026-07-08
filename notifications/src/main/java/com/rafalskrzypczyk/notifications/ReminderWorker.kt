package com.rafalskrzypczyk.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import com.rafalskrzypczyk.core.utils.toDateOnly
import com.rafalskrzypczyk.score.domain.ScoreRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import java.util.Calendar
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
 * Ocenia warunki i — na tym etapie — wysyła wyłącznie Daily Reminder,
 * gdy dzień nie jest jeszcze zaliczony (streak ≠ DONE dziś). Po każdym uruchomieniu
 * przeplanowuje się na kolejny dzień.
 */
class ReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val deps = EntryPointAccessors.fromApplication(appContext, ReminderWorkerEntryPoint::class.java)

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

        if (scoreResponse is Response.Success && !isDoneToday(scoreResponse.data.lastStreakUpdateDate)) {
            deps.notifier().show(
                notificationId = NotificationIds.DAILY_REMINDER,
                title = applicationContext.getString(R.string.notification_daily_title),
                text = DailyReminderContent.random(),
                destination = NotificationDestination.HOME
            )
        }
        // Response.Error / brak danych → zachowawczo nic nie wysyłamy.

        reminderScheduler.schedule()
        return Result.success()
    }

    private fun isDoneToday(lastStreakUpdateDate: Date?): Boolean {
        if (lastStreakUpdateDate == null) return false
        val today = Calendar.getInstance().time.toDateOnly()
        return lastStreakUpdateDate.toDateOnly() == today
    }
}
