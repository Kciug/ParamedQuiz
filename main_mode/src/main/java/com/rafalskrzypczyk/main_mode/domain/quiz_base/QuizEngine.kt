package com.rafalskrzypczyk.main_mode.domain.quiz_base

import com.rafalskrzypczyk.main_mode.domain.models.Question

class QuizEngine(
    private val useCases: BaseQuizUseCases
) {
    private var questions: List<Question> = emptyList()
    private var currentIndex: Int = 0
    private var correctAnswers: Int = 0

    fun setQuestions(newQuestions: List<Question>) {
        questions = newQuestions
        currentIndex = 0
        correctAnswers = 0
    }

    fun getAllQuestions(): List<Question> = questions

    fun getCurrentQuestion(): Question? = questions.getOrNull(currentIndex)

    fun getCurrentQuestionNumber() = currentIndex + 1


    fun nextQuestion(): Question? = questions.getOrNull(++currentIndex)

    fun submitAnswer(selectedIds: List<Long>): Boolean {
        val isCorrect = useCases.evaluateAnswers(questions[currentIndex], selectedIds)
        if(isCorrect) correctAnswers++
        return isCorrect
    }

    fun getCorrectAnswers() = correctAnswers

    fun getQuestionsCount() = questions.size

    fun isFinished() = currentIndex >= questions.size
}