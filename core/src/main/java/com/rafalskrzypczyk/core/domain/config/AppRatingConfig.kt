package com.rafalskrzypczyk.core.domain.config

/**
 * Strojalne progi wyświetlania prośby o ocenę aplikacji.
 */
object AppRatingConfig {
    /** Minimalna liczba dni od instalacji. */
    const val MIN_DAYS_SINCE_INSTALL = 3

    /** Minimalna liczba ukończonych quizów. */
    const val MIN_COMPLETED_QUIZZES = 5

    /** Minimalny odstęp (w dniach) między kolejnymi pokazaniami. */
    const val MIN_DAYS_BETWEEN_PROMPTS = 14
}
