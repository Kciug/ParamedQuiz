package com.rafalskrzypczyk.main_mode.domain.daily_exercise

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.main_mode.domain.MainModeRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.map

class GetAllQuestionsShuffledUC @Inject constructor(
    private val repository: MainModeRepository
) {
    operator fun invoke() = repository.getAllQuestions().map {
        if(it is Response.Success){
            Response.Success(it.data.shuffled())
        } else {
            it
        }
    }
}