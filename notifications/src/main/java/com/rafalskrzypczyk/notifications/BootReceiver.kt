package com.rafalskrzypczyk.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface BootReceiverEntryPoint {
    fun reminderScheduler(): ReminderScheduler
}

/**
 * Alarmy nie przeżywają reboota (ani zmiany czasu/strefy, ani aktualizacji apki), więc po tych
 * zdarzeniach ponownie uzbrajamy przypomnienie przez [ReminderScheduler.ensureScheduled].
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Reagujemy tylko na oczekiwane akcje systemowe (protected broadcasts) — ignorujemy
        // spreparowane intenty bez akcji / z inną akcją (lint UnsafeProtectedBroadcastReceiver).
        if (intent.action !in HANDLED_ACTIONS) return

        EntryPointAccessors.fromApplication(
            context.applicationContext,
            BootReceiverEntryPoint::class.java
        ).reminderScheduler().ensureScheduled()
    }

    companion object {
        private val HANDLED_ACTIONS = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_TIME_CHANGED,
        )
    }
}
