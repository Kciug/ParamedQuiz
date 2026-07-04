package com.rafalskrzypczyk.revisions.domain.engine

import com.rafalskrzypczyk.revisions.domain.models.RevisionQuestion

sealed interface RevisionAnswerResult {
    val question: RevisionQuestion
    val isFirstAttempt: Boolean
    val isSessionFinished: Boolean

    data class Correct(
        override val question: RevisionQuestion,
        override val isFirstAttempt: Boolean,
        override val isSessionFinished: Boolean
    ) : RevisionAnswerResult

    data class IncorrectQueued(
        override val question: RevisionQuestion,
        override val isFirstAttempt: Boolean,
        override val isSessionFinished: Boolean
    ) : RevisionAnswerResult

    data class HitErrorLimit(
        override val question: RevisionQuestion,
        override val isFirstAttempt: Boolean,
        override val isSessionFinished: Boolean
    ) : RevisionAnswerResult
}

class RevisionsSessionEngine {

    private val queue = mutableListOf<RevisionQuestion>()
    private val attemptedQuestionIds = mutableSetOf<Long>()
    private val errorCounts = mutableMapOf<Long, Int>()
    private val failedQuestionIds = mutableSetOf<Long>()
    private val playedQuestions = mutableListOf<RevisionQuestion>()

    private var initialSize = 0
    private var correctAnswersCount = 0
    private var totalPointsEarned = 0
    private var isFirstInteractionCorrectCount = 0

    fun startSession(questions: List<RevisionQuestion>) {
        queue.clear()
        queue.addAll(questions)
        attemptedQuestionIds.clear()
        errorCounts.clear()
        failedQuestionIds.clear()
        playedQuestions.clear()
        playedQuestions.addAll(questions)
        initialSize = questions.size
        correctAnswersCount = 0
        totalPointsEarned = 0
        isFirstInteractionCorrectCount = 0
    }

    fun getCurrentQuestion(): RevisionQuestion? {
        return queue.firstOrNull()
    }

    fun submitAnswer(answeredCorrectly: Boolean): RevisionAnswerResult? {
        val currentQuestion = getCurrentQuestion() ?: return null
        val questionId = currentQuestion.id

        val isFirstAttempt = !attemptedQuestionIds.contains(questionId)
        if (isFirstAttempt) {
            attemptedQuestionIds.add(questionId)
            if (answeredCorrectly) {
                isFirstInteractionCorrectCount++
            }
        }

        val result: RevisionAnswerResult
        if (answeredCorrectly) {
            queue.removeAt(0)
            correctAnswersCount++
            val isSessionFinished = queue.isEmpty()
            result = RevisionAnswerResult.Correct(
                question = currentQuestion,
                isFirstAttempt = isFirstAttempt,
                isSessionFinished = isSessionFinished
            )
        } else {
            val currentErrors = (errorCounts[questionId] ?: 0) + 1
            errorCounts[questionId] = currentErrors

            if (currentErrors >= 3) {
                queue.removeAt(0)
                failedQuestionIds.add(questionId)
                val isSessionFinished = queue.isEmpty()
                result = RevisionAnswerResult.HitErrorLimit(
                    question = currentQuestion,
                    isFirstAttempt = isFirstAttempt,
                    isSessionFinished = isSessionFinished
                )
            } else {
                queue.removeAt(0)
                queue.add(currentQuestion)
                result = RevisionAnswerResult.IncorrectQueued(
                    question = currentQuestion,
                    isFirstAttempt = isFirstAttempt,
                    isSessionFinished = false
                )
            }
        }

        return result
    }

    fun addEarnedPoints(points: Int) {
        totalPointsEarned += points
    }

    fun getInitialSize(): Int = initialSize

    fun getCurrentQueueSize(): Int = queue.size

    fun getQueueIds(): Set<Long> = queue.map { it.id }.toSet()

    fun getPlayedQuestions(): List<RevisionQuestion> = playedQuestions

    fun getFailedQuestionIds(): Set<Long> = failedQuestionIds

    fun getAttemptedQuestionIds(): Set<Long> = attemptedQuestionIds

    fun getCorrectAnswersCount(): Int = correctAnswersCount

    fun getFirstInteractionCorrectCount(): Int = isFirstInteractionCorrectCount

    fun getTotalPointsEarned(): Int = totalPointsEarned
}
