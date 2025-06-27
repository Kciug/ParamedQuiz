package com.rafalskrzypczyk.swipe_mode.domain

import com.rafalskrzypczyk.core.api_response.Response
import kotlinx.coroutines.flow.Flow

interface SwipeModeRepository {
    fun getSwipeQuestions() : Flow<Response<List<SwipeQuestion>>>
    fun getUpdatedQuestions() : Flow<List<SwipeQuestion>>
}