package com.rafalskrzypczyk.swipe_mode.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUpdatedSwipeQuestionsUC @Inject constructor(
    private val repository: SwipeModeRepository
) {
    operator fun invoke(): Flow<List<SwipeQuestion>> = repository.getUpdatedQuestions(isTrial = false)
}
