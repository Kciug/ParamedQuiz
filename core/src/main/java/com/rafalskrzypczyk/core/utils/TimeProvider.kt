package com.rafalskrzypczyk.core.utils

import java.util.Date
import javax.inject.Inject

/**
 * Wstrzykiwalne źródło „teraz". Pozwala testom sterować czasem (streak / ćwiczenie dnia),
 * a w produkcji zwraca systemowy zegar. Konsumenci: [com.rafalskrzypczyk.core] i moduły zależne.
 */
interface TimeProvider {
    fun now(): Date
}

class SystemTimeProvider @Inject constructor() : TimeProvider {
    override fun now(): Date = Date()
}
