package com.rafalskrzypczyk.score.domain

object ScorePoints {
    private const val ZERO = 0

    fun calculateForQuestion(
        isCorrect: Boolean,
        firstCorrect: Boolean,
        correctPoints: Int,
        firstCorrectPoints: Int
    ) : Int {
        return if(isCorrect) {
            if(firstCorrect) firstCorrectPoints else correctPoints
        } else ZERO
    }
}
