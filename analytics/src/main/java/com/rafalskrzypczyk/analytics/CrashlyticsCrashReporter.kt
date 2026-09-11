package com.rafalskrzypczyk.analytics

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rafalskrzypczyk.core.error.CrashReporter

/** Produkcyjna implementacja [CrashReporter]. */
class CrashlyticsCrashReporter(
    private val crashlytics: FirebaseCrashlytics,
) : CrashReporter {
    override fun log(message: String) = crashlytics.log(message)

    override fun recordException(throwable: Throwable) = crashlytics.recordException(throwable)

    override fun setCustomKey(key: String, value: String) = crashlytics.setCustomKey(key, value)

    override fun setCollectionEnabled(enabled: Boolean) {
        crashlytics.isCrashlyticsCollectionEnabled = enabled
    }

    override fun deleteUnsentReports() = crashlytics.deleteUnsentReports()
}
