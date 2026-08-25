package com.rafalskrzypczyk.auth.domain

import android.content.Context

/**
 * Izoluje Credential Manager od repozytorium, dzięki czemu ścieżka logowania Google
 * daje się przetestować bez urządzenia.
 *
 * [getGoogleIdToken] zwraca surowy token Google ID albo null, gdy zwrócone poświadczenie
 * nie jest tokenem Google. Wyjątki Credential Managera propagują do wywołującego,
 * który mapuje je na konkretne warianty błędu.
 */
interface GoogleCredentialsProvider {
    suspend fun getGoogleIdToken(context: Context): String?
    suspend fun clearCredentialState(context: Context)
}
