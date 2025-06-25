package com.rafalskrzypczyk.main_mode.domain.quiz

import com.rafalskrzypczyk.main_mode.domain.MainModeRepository
import javax.inject.Inject

class GetUpdatedQuestionsUC @Inject constructor(
    private val repository: MainModeRepository
) {
    operator fun invoke() = repository.getUpdatedQuestions()
}