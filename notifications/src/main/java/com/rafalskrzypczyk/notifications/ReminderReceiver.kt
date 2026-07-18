package com.rafalskrzypczyk.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.milliseconds

/**
 * Zależności odbiornika wstrzykiwane przez Hilt [EntryPoint] (analogicznie do
 * [ContentMessagingEntryPoint]) — zależności są `implementation`
 * w :notifications, więc nie chcemy ich rozwiązywać w agregacji Hilta w :app.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface ReminderReceiverEntryPoint {
    fun reminderEvaluator(): ReminderEvaluator
    fun reminderScheduler(): ReminderScheduler
}

/**
 * Odbiornik alarmu z [ReminderScheduler]. Najpierw przeplanowuje kolejny dzień (odporność na
 * ~10 s limit [goAsync] i ubicie procesu w trakcie), potem ocenia i wysyła powiadomienie.
 */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val deps = EntryPointAccessors.fromApplication(
            context.applicationContext,
            ReminderReceiverEntryPoint::class.java
        )
        val pendingResult = goAsync()
        scope.launch {
            try {
                deps.reminderScheduler().ensureScheduled()
                withTimeoutOrNull(EVAL_TIMEOUT_MS.milliseconds) {
                    deps.reminderEvaluator().evaluateAndNotify()
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_FIRE = "com.rafalskrzypczyk.notifications.action.REMINDER_FIRE"
        private const val EVAL_TIMEOUT_MS = 8_000L
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}
