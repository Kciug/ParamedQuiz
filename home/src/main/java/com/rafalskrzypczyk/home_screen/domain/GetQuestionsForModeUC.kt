package com.rafalskrzypczyk.home_screen.domain

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.home_screen.domain.models.QuizMode
import com.rafalskrzypczyk.home_screen.domain.models.SimpleQuestion
import com.rafalskrzypczyk.home_screen.domain.models.toSimpleQuestion
import com.rafalskrzypczyk.main_mode.domain.MainModeRepository
import com.rafalskrzypczyk.swipe_mode.domain.SwipeModeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetQuestionsForModeUC @Inject constructor(
    private val mainModeRepository: MainModeRepository,
    private val swipeModeRepository: SwipeModeRepository,
) {
    operator fun invoke(mode: QuizMode): Flow<Response<List<SimpleQuestion>>> {
        return when(mode) {
            QuizMode.MainMode -> mainModeRepository.getAllQuestions().map { response ->
                when(response) {
                    is Response.Error -> Response.Error(response.error)
                    Response.Loading -> Response.Loading
                    is Response.Success -> Response.Success(response.data.map { it.toSimpleQuestion() })
                }
            }
            QuizMode.SwipeMode -> swipeModeRepository.getSwipeQuestions().map { response ->
                when (response) {
                    is Response.Error -> Response.Error(response.error)
                    Response.Loading -> Response.Loading
                    is Response.Success -> Response.Success(response.data.map { it.toSimpleQuestion() })
                }
            }
        }
    }
}