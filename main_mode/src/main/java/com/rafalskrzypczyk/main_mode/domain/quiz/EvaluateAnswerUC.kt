package com.rafalskrzypczyk.main_mode.domain.quiz

import com.rafalskrzypczyk.main_mode.domain.models.Question
import javax.inject.Inject

class EvaluateAnswerUC @Inject constructor() {
    operator fun invoke(question: Question, selectedAnswerIds: List<Long>): Boolean {
        val correctAnswers = question.answers.filter { it.isCorrect }

        if(selectedAnswerIds.size != correctAnswers.size) return false
        return correctAnswers.all { ca -> selectedAnswerIds.contains(ca.id) }
    }
}