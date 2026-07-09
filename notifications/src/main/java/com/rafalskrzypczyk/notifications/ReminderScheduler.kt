package com.rafalskrzypczyk.notifications

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Planuje lokalne przypomnienia przez WorkManager jako unikalny, samo-przeplanowujący się
 * OneTimeWork ustawiony na najbliższą wybraną godzinę.
 */
@Singleton
class ReminderScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val sharedPrefs: SharedPreferencesApi
) {
    /** Planuje przypomnienie, gdy włączone; w przeciwnym razie anuluje. Wołane przy starcie aplikacji. */
    fun ensureScheduled() {
        if (sharedPrefs.isNotificationsEnabled()) schedule() else cancel()
    }

    /** Planuje (lub aktualizuje) unikalne przypomnienie na najbliższą ustawioną godzinę. */
    fun schedule() {
        val delay = computeInitialDelayMillis(
            sharedPrefs.getReminderHour(),
            sharedPrefs.getReminderMinute()
        )
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun cancel() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    /** Debug: uruchamia workera natychmiast (test „oceń i zdecyduj" bez czekania na godzinę). */
    fun debugRunNow() {
        val request = OneTimeWorkRequestBuilder<ReminderWorker>().build()
        WorkManager.getInstance(context).enqueue(request)
    }

    private fun computeInitialDelayMillis(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (!after(now)) add(Calendar.DAY_OF_MONTH, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }

    companion object {
        private const val WORK_NAME = "daily_reminder_work"
    }
}
