package com.rafalskrzypczyk.revisions.domain

/**
 * Strojalne parametry modułu powtórek.
 */
object RevisionsConfig {
    /** Ile razy pytanie może zostać błędnie odpowiedziane, zanim zostanie zdjęte z sesji. */
    const val MAX_ERRORS_PER_QUESTION = 3

    /** Minimalna liczba odpowiedzianych pytań, by odblokować tryb tłumaczeń. */
    const val MIN_ANSWERED_FOR_TRANSLATION = 10

    /** Dostępne rozmiary sesji powtórek do wyboru. */
    val QUESTION_LIMIT_OPTIONS = listOf(10, 20, 50, 100)

    /** Domyślnie zaznaczony limit sesji. */
    val DEFAULT_SELECTED_LIMIT: Int = QUESTION_LIMIT_OPTIONS.first()
}
