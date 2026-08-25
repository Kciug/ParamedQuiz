package com.rafalskrzypczyk.core.network

/**
 * Odpowiada na pytanie, czy urzadzenie ma w tej chwili dzialajace polaczenie z internetem.
 *
 * Sluzy do odroznienia awarii po stronie backendu od zwyklego braku sieci. Firestore
 * w obu przypadkach zwraca ten sam kod UNAVAILABLE.
 */
interface NetworkMonitor {
    fun isOnline(): Boolean
}
