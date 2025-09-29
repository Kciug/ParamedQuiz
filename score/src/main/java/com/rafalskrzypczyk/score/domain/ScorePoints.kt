package com.rafalskrzypczyk.score.domain

object ScorePoints {
    const val CORRECT = 100
    const val FIRST_CORRECT = 500
    const val ZERO = 0

    fun calculateForQuestion(isCorrect: Boolean, firstCorrect: Boolean) : Int {
        return if(isCorrect) {
            if(firstCorrect) FIRST_CORRECT else CORRECT
        } else ZERO
    }
}