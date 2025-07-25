package com.rafalskrzypczyk.score.domain

import com.rafalskrzypczyk.score.ScoreManager
import javax.inject.Inject

class UpdateSeenQuestionUC @Inject constructor(
    private val scoreManager: ScoreManager
) {
    operator fun invoke(questionId: Long, answeredCorrectly: Boolean) {
        val currentScore = scoreManager.getScore()

        val updatedSeenQuestions = currentScore.seenQuestions.toMutableList()
        val index = updatedSeenQuestions.indexOfFirst { it.questionId == questionId }

        if (index >= 0) {
            val existing = updatedSeenQuestions[index]
            updatedSeenQuestions[index] = existing.copy(
                timesSeen = existing.timesSeen + 1,
                timesCorrect = existing.timesCorrect + if (answeredCorrectly) 1 else 0
            )
        } else {
            updatedSeenQuestions.add(QuestionAnnotation.new(questionId, answeredCorrectly))
        }

        val newScore = currentScore.copy(
            score = currentScore.score + if (answeredCorrectly) 100 else 0,
            seenQuestions = updatedSeenQuestions
        )

        scoreManager.updateScore(newScore)
    }
}
