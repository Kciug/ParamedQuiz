package com.rafalskrzypczyk.notifications

/**
 * Cel deep-linku przekazywany w powiadomieniu jako extra Intentu.
 * Mapowany na trasę nawigacji po tapnięciu w powiadomienie (patrz MainActivity).
 *
 * Na razie obsługujemy tylko [HOME]; kolejne cele (np. powtórki) dojdą w następnych etapach.
 */
enum class NotificationDestination {
    HOME;

    companion object {
        const val EXTRA_DESTINATION = "notification_destination"

        fun fromExtra(value: String?): NotificationDestination? =
            entries.firstOrNull { it.name == value }
    }
}
