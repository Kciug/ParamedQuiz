package com.rafalskrzypczyk.swipe_mode.presentation

/**
 * Strojalne progi werdyktu szybkość/dokładność w trybie swipe.
 */
object SwipeVerdictConfig {
    /** Poniżej tego stosunku czasu (błędne/poprawne) użytkownik odpowiada impulsywnie. */
    const val IMPULSIVE_RATIO = 0.7f

    /** Powyżej tego stosunku czasu (błędne/poprawne) użytkownik się waha. */
    const val HESITANT_RATIO = 1.3f

    /** Minimalna liczba błędów, by w ogóle wystawić werdykt. */
    const val MIN_ERRORS_FOR_VERDICT = 3
}
