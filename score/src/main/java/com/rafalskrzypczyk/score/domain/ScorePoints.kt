package com.rafalskrzypczyk.score.domain

object ScorePoints {
    private const val ZERO = 0

    fun calculateForQuestion(isCorrect: Boolean, firstCorrect: Boolean) : Int {
        return if(isCorrect) {
            if(firstCorrect) ScoreConfig.FIRST_CORRECT else ScoreConfig.CORRECT
        } else ZERO
    }
}
