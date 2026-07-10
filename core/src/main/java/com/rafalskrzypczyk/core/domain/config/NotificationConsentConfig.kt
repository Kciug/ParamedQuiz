package com.rafalskrzypczyk.core.domain.config

/**
 * Strojalne progi wyświetlania pre-permission promptu zgody na powiadomienia (priming).
 */
object NotificationConsentConfig {
    /** Minimalna liczba ukończonych quizów, zanim pokażemy prompt. */
    const val MIN_COMPLETED_QUIZZES = 1

    /** Maksymalna liczba pokazań (początkowe + ponowienia). */
    const val MAX_PROMPTS = 3

    /** Minimalny odstęp (w dniach) między kolejnymi pokazaniami. */
    const val MIN_DAYS_BETWEEN_PROMPTS = 7
}
