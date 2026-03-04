package com.rafalskrzypczyk.cem_mode.domain.use_cases

import com.rafalskrzypczyk.cem_mode.domain.CemRepository
import com.rafalskrzypczyk.core.api_response.Response
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetShuffledQuestionsForCemCategoryUC @Inject constructor(
    private val repository: CemRepository
) {
    operator fun invoke(categoryId: Long) = repository.getAllCemQuestions().map {
        if(it is Response.Success){
            Response.Success(it.data.filter { it.assignedCategoriesIds.contains(categoryId) }.shuffled())
        } else {
            it
        }
    }
}