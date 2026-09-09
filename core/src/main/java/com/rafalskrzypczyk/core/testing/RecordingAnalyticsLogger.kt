package com.rafalskrzypczyk.core.testing

import com.rafalskrzypczyk.core.analytics.AnalyticsConsent
import com.rafalskrzypczyk.core.analytics.AnalyticsEvent
import com.rafalskrzypczyk.core.analytics.AnalyticsLogger
import com.rafalskrzypczyk.core.analytics.AnalyticsUserProperty

/**
 * Atrapa [AnalyticsLogger] zapamiętująca wysłane zdarzenia. Trzymana w kodzie produkcyjnym `core`,
 * bo w projekcie nie ma `testFixtures`, a rekorder jest potrzebny zarówno w harnessie E2E
 * (moduł `app`), jak i w testach modułowych — tak samo jak [TestTags] i `NoOpFeedbackManager`.
 *
 * Nie używa `android.util.Log`, więc działa w każdym module.
 */
class RecordingAnalyticsLogger : AnalyticsLogger {
    private val recordedEvents = mutableListOf<AnalyticsEvent>()
    private val recordedProperties = mutableMapOf<AnalyticsUserProperty, String>()

    val events: List<AnalyticsEvent> get() = recordedEvents.toList()
    val userProperties: Map<AnalyticsUserProperty, String> get() = recordedProperties.toMap()
    var consent: AnalyticsConsent? = null
        private set

    override fun log(event: AnalyticsEvent) {
        recordedEvents += event
    }

    override fun setUserProperty(property: AnalyticsUserProperty, value: String) {
        recordedProperties[property] = value
    }

    override fun setConsent(consent: AnalyticsConsent) {
        this.consent = consent
    }

    /** Zdarzenia danego typu, w kolejności wysłania. */
    inline fun <reified T : AnalyticsEvent> eventsOfType(): List<T> = events.filterIsInstance<T>()

    fun eventNames(): List<String> = events.map { it.name }

    fun clear() {
        recordedEvents.clear()
        recordedProperties.clear()
        consent = null
    }
}
