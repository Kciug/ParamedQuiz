package com.rafalskrzypczyk.paramedquiz.e2e.fakes

import com.rafalskrzypczyk.core.utils.TimeProvider
import java.util.Date

/**
 * Sterowalne źródło czasu dla testów (streak / ćwiczenie dnia). Domyślnie „teraz"; testy ustawiają
 * [current], by symulować konkretny dzień (a w przyszłości — upływ czasu, np. „jutro").
 */
class FakeTimeProvider : TimeProvider {
    var current: Date = Date()
    override fun now(): Date = current
}
