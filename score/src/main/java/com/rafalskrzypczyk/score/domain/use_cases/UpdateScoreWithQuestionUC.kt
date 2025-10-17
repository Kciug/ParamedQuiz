package com.rafalskrzypczyk.score.domain.use_cases

import com.rafalskrzypczyk.score.domain.QuestionAnnotation
import com.rafalskrzypczyk.score.domain.ScoreManager
import com.rafalskrzypczyk.score.domain.ScorePoints
import javax.inject.Inject

class UpdateScoreWithQuestionUC @Inject constructor(
    private val scoreManager: ScoreManager
) {
    operator fun invoke(questionId: Long, answeredCorrectly: Boolean) : Int {
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
            updatedSeenQuestions.add(QuestionAnnotation.Companion.new(questionId, answeredCorrectly))
            firstCorrectAnswer = answeredCorrectly
        }

        val earnedPoints = ScorePoints.calculateForQuestion(answeredCorrectly, firstCorrectAnswer)

        val newScore = currentScore.copy(
            score = currentScore.score + earnedPoints,
            seenQuestions = updatedSeenQuestions
        )

        scoreManager.updateScore(newScore)

        return earnedPoints
    }
}
