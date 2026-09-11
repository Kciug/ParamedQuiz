package com.rafalskrzypczyk.core.error

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Cienki interfejs nad raportowaniem awarii (Crashlytics). Trzymany w `core`, bo implementacja
 * ErrorLogger tutaj żyje, a `core` nie może zależeć od modułów firebase-aware.
 *
 * Zbieranie podlega tej samej zgodzie co analityka — sterowaniem zajmuje się
 * [com.rafalskrzypczyk.core.analytics.AnalyticsConsentManager].
 */
interface CrashReporter {
    /** Ślad (breadcrumb) dołączany do kolejnego raportu. */
    fun log(message: String)

    /** Niefatalny wyjątek. */
    fun recordException(throwable: Throwable)

    fun setCustomKey(key: String, value: String)

    /** Globalny przełącznik zbierania. Wartość jest trwała po stronie SDK. */
    fun setCollectionEnabled(enabled: Boolean)

    /**
     * Usuwa raporty czekające na wysyłkę. Bez tego wycofanie zgody zostawia je na dysku,
     * a wyszłyby przy ponownym jej udzieleniu.
     */
    fun deleteUnsentReports()
}

/** Implementacja dla buildów debug i dla testów — nie wysyła niczego. */
@Singleton
class NoOpCrashReporter @Inject constructor() : CrashReporter {
    override fun log(message: String) = Unit
    override fun recordException(throwable: Throwable) = Unit
    override fun setCustomKey(key: String, value: String) = Unit
    override fun setCollectionEnabled(enabled: Boolean) = Unit
    override fun deleteUnsentReports() = Unit
}
