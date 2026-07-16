package com.rafalskrzypczyk.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Planuje lokalne przypomnienia przez [AlarmManager] jako jednorazowy alarm na najbliższą
 * wybraną godzinę. AlarmManager (w przeciwieństwie do WorkManagera/JobSchedulera) trzyma alarm
 * w systemie poza procesem apki i cold-startuje ją przy odpaleniu — dzięki czemu przypomnienie
 * przeżywa swipe z listy ostatnich na agresywnych OEM-ach (np. Samsung One UI).
 *
 * Używamy [AlarmManager.setAndAllowWhileIdle] (przebija Doze, bez uprawnienia exact-alarm);
 * kosztem dopuszczalnego kilkuminutowego poślizgu względem wybranej godziny. Alarm jest jednorazowy,
 * więc [ReminderReceiver] po odpaleniu przeplanowuje kolejny (a reboot obsługuje [BootReceiver]).
 */
@Singleton
class ReminderScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val sharedPrefs: SharedPreferencesApi
) {
    private val alarmManager: AlarmManager
        get() = context.getSystemService(AlarmManager::class.java)

    /**
     * Planuje przypomnienie, gdy powiadomienia i kategoria „Przypomnienia" są włączone;
     * w przeciwnym razie anuluje. Wołane przy starcie aplikacji, po zmianie ustawień i po reboocie.
     */
    fun ensureScheduled() {
        if (sharedPrefs.isNotificationsEnabled() && sharedPrefs.isRemindersEnabled()) schedule() else cancel()
    }

    /** Planuje (lub aktualizuje) alarm na najbliższą ustawioną godzinę. */
    fun schedule() {
        val triggerAtMillis = computeNextTriggerMillis(
            sharedPrefs.getReminderHour(),
            sharedPrefs.getReminderMinute()
        )
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent()
        )
    }

    fun cancel() {
        alarmManager.cancel(pendingIntent())
    }

    private fun pendingIntent(): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).setAction(ReminderReceiver.ACTION_FIRE)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun computeNextTriggerMillis(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (!after(now)) add(Calendar.DAY_OF_MONTH, 1)
        }
        return target.timeInMillis
    }

    companion object {
        private const val REQUEST_CODE = 1001
    }
}
