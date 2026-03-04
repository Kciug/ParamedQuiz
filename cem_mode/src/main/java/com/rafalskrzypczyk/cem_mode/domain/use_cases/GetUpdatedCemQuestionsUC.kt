package com.rafalskrzypczyk.cem_mode.domain.use_cases

import com.rafalskrzypczyk.cem_mode.domain.CemRepository
import javax.inject.Inject

class GetUpdatedCemQuestionsUC @Inject constructor(
    private val repository: CemRepository
) {
    operator fun invoke() = repository.getUpdatedCemQuestions()
}