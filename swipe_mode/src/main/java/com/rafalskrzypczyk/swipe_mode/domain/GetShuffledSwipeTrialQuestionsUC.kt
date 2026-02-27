package com.rafalskrzypczyk.swipe_mode.domain

import com.rafalskrzypczyk.core.api_response.Response
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetShuffledSwipeTrialQuestionsUC @Inject constructor(
    private val repository: SwipeModeRepository
) {
    operator fun invoke() = repository.getSwipeQuestions().map {
        if(it is Response.Success){
            val trialQuestions = it.data.filter { q -> q.isFree }
            Response.Success(trialQuestions.shuffled())
        } else {
            it
        }
    }
}
