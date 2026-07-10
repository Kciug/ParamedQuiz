package com.rafalskrzypczyk.score.domain

/**
 * Strojalne parametry modułu wyników (punktacja, seria).
 */
object ScoreConfig {
    const val CORRECT = 100
    const val FIRST_CORRECT = 300

    /** Ile pytań trzeba zaliczyć w sesji, aby podbić serię (streak). */
    const val STREAK_POINTS_THRESHOLD = 3
}
