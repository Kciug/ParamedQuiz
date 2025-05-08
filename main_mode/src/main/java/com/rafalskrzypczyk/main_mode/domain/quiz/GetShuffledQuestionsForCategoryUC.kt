package com.rafalskrzypczyk.main_mode.domain.quiz

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.main_mode.domain.MainModeRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetShuffledQuestionsForCategoryUC @Inject constructor(
    private val repository: MainModeRepository
) {
    operator fun invoke(categoryId: Long) = repository.getAllQuestions().map {
        if(it is Response.Success){
            Response.Success(it.data.filter { it.assignedCategoriesIds.contains(categoryId) }.shuffled())
        } else {
            it
        }
    }
}