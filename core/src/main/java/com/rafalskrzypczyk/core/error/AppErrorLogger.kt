package com.rafalskrzypczyk.core.error

import android.util.Log
import com.rafalskrzypczyk.core.analytics.AnalyticsEvent
import com.rafalskrzypczyk.core.analytics.AnalyticsLogger
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Domyślna implementacja [ErrorLogger]: logcat + Crashlytics + zdarzenie `app_error`.
 *
 * Wszystkie błędy w aplikacji przechodzą przez jeden punkt (reguła "loguj raz i tylko raz"),
 * więc to jedno miejsce daje pokrycie diagnostyką całej aplikacji.
 *
 * Nie konstruować w testach jednostkowych modułów bibliotecznych — [Log] bywa tam "not mocked".
 */
@Singleton
class AppErrorLogger @Inject constructor(
    private val analytics: AnalyticsLogger,
    private val crashReporter: CrashReporter,
) : ErrorLogger {
    override fun log(origin: String, error: AppError, cause: Throwable?) {
        val message = "$origin | $error"
        if (cause != null) Log.e(TAG, message, cause) else Log.e(TAG, message)

        crashReporter.log(message)
        cause?.let { crashReporter.recordException(it) }

        analytics.log(
            AnalyticsEvent.AppErrorOccurred(
                origin = origin,
                errorType = error.analyticsName(),
            )
        )
    }

    private companion object {
        const val TAG = "AppError"
    }
}
