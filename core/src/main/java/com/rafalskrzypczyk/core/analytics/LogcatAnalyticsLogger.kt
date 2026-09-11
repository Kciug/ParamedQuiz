package com.rafalskrzypczyk.core.analytics

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementacja dla buildów debug — wypisuje zdarzenia do logcat pod tagiem [TAG].
 *
 * Sterowanie zbieraniem jest tu bez znaczenia (nic nie wychodzi poza urządzenie), ale
 * implementujemy [AnalyticsControls] dla symetrii z wariantem produkcyjnym.
 *
 * Nie konstruować w testach jednostkowych: [Log] bywa "not mocked" poza modułem `app`.
 * W testach używaj [com.rafalskrzypczyk.core.testing.RecordingAnalyticsLogger] albo mocka interfejsu.
 */
@Singleton
class LogcatAnalyticsLogger @Inject constructor() : AnalyticsBackend {
    override fun log(event: AnalyticsEvent) {
        Log.d(TAG, "${event.name} ${event.params}")
    }

    override fun setUserProperty(property: AnalyticsUserProperty, value: String) {
        Log.d(TAG, "property ${property.propertyName} = $value")
    }

    override fun setConsent(consent: AnalyticsConsent) {
        Log.d(TAG, "consent $consent")
    }

    override fun setCollectionEnabled(enabled: Boolean) {
        Log.d(TAG, "collection enabled = $enabled")
    }

    override fun resetAnalyticsData() {
        Log.d(TAG, "analytics data reset")
    }

    private companion object {
        const val TAG = "Analytics"
    }
}
