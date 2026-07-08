package com.rafalskrzypczyk.notifications

import android.content.Context
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.core.app.NotificationManagerCompat

object NotificationPermission {
    /**
     * Uprawnienie wymagane od Androida 13 (API 33). Poniżej — brak runtime permission.
     * Literał zamiast [android.Manifest.permission.POST_NOTIFICATIONS], by uniknąć ostrzeżenia
     * NewApi przy minSdk 24 (i tak jest to stała kompilacyjna o tej samej wartości).
     */
    const val POST_NOTIFICATIONS: String = "android.permission.POST_NOTIFICATIONS"

    /** Czy na tej wersji systemu trzeba prosić o zgodę runtime (API 33+). */
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
    fun requiresRuntimePermission(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    /**
     * True, gdy aplikacja może realnie wyświetlać powiadomienia — uwzględnia zarówno
     * uprawnienie runtime, jak i systemowy przełącznik powiadomień aplikacji.
     */
    fun areNotificationsEnabled(context: Context): Boolean =
        NotificationManagerCompat.from(context).areNotificationsEnabled()
}
