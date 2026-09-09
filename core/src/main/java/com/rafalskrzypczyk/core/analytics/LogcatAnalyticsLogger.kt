package com.rafalskrzypczyk.core.analytics

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementacja [AnalyticsLogger] dla buildów debug — wypisuje zdarzenia do logcat pod tagiem [TAG].
 *
 * Nie konstruować w testach jednostkowych: [Log] bywa "not mocked" poza modułem `app`.
 * W testach używaj [com.rafalskrzypczyk.core.testing.RecordingAnalyticsLogger] albo mocka interfejsu.
 */
@Singleton
class LogcatAnalyticsLogger @Inject constructor() : AnalyticsLogger {
    override fun log(event: AnalyticsEvent) {
        Log.d(TAG, "${event.name} ${event.params}")
    }

    override fun setUserProperty(property: AnalyticsUserProperty, value: String) {
        Log.d(TAG, "property ${property.propertyName} = $value")
    }

    override fun setConsent(consent: AnalyticsConsent) {
        Log.d(TAG, "consent $consent")
    }

    private companion object {
        const val TAG = "Analytics"
    }
}
