package com.rafalskrzypczyk.core.analytics

import com.rafalskrzypczyk.core.BuildConfig
import com.rafalskrzypczyk.core.utils.TimeProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Ostatnie zdarzenia i właściwości użytkownika, które **przeszły przez bramkę zgody** — podgląd
 * dla opcji deweloperskich, bez kabla i `adb shell setprop`.
 *
 * Zasilany z [ConsentGatedAnalyticsLogger] za bramką, nie przed: bufor ma pokazywać to, co
 * faktycznie wyszło, więc pusta lista przy wycofanej zgodzie jest testem bramki, a nie błędem.
 * W buildach bez opcji deweloperskich nic nie zapisuje.
 */
@Singleton
class RecentAnalyticsEvents @Inject constructor(
    private val timeProvider: TimeProvider,
) {
    sealed interface Entry {
        val atMillis: Long

        data class Event(
            override val atMillis: Long,
            val name: String,
            val params: Map<String, Any>,
        ) : Entry

        data class Property(
            override val atMillis: Long,
            val name: String,
            val value: String,
        ) : Entry
    }

    private val _entries = MutableStateFlow<List<Entry>>(emptyList())

    /** Od najnowszego. */
    val entries: StateFlow<List<Entry>> = _entries

    fun record(event: AnalyticsEvent) {
        add(Entry.Event(timeProvider.now().time, event.name, event.params))
    }

    fun record(property: AnalyticsUserProperty, value: String) {
        add(Entry.Property(timeProvider.now().time, property.propertyName, value))
    }

    fun clear() {
        _entries.value = emptyList()
    }

    private fun add(entry: Entry) {
        if (!BuildConfig.DEV_OPTIONS_ENABLED) return
        _entries.update { current -> (listOf(entry) + current).take(CAPACITY) }
    }

    companion object {
        const val CAPACITY = 50
    }
}
