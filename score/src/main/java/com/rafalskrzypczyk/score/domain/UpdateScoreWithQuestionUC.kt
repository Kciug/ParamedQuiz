package com.rafalskrzypczyk.score.domain

import com.rafalskrzypczyk.score.ScoreManager
import com.rafalskrzypczyk.score.ScorePoints
import javax.inject.Inject

class UpdateScoreWithQuestionUC @Inject constructor(
    private val scoreManager: ScoreManager
) {
    operator fun invoke(questionId: Long, answeredCorrectly: Boolean) {
        val currentScore = scoreManager.getScore()

        var firstCorrectAnswer = false

        val updatedSeenQuestions = currentScore.seenQuestions
            .map {
                if (it.questionId == questionId) {
                    firstCorrectAnswer = it.timesCorrect == 0L && answeredCorrectly
                    it.copy(
                        timesSeen = it.timesSeen + 1,
                        timesCorrect = it.timesCorrect + if (answeredCorrectly) 1 else 0
                    )
                } else it
            }
            .toMutableList()

        if (updatedSeenQuestions.none { it.questionId == questionId }) {
            updatedSeenQuestions.add(QuestionAnnotation.new(questionId, answeredCorrectly))
            firstCorrectAnswer = answeredCorrectly
        }

        val newScore = currentScore.copy(
            score = currentScore.score + ScorePoints.calculateForQuestion(answeredCorrectly, firstCorrectAnswer),
            seenQuestions = updatedSeenQuestions
        )

        scoreManager.updateScore(newScore)
    }
}
