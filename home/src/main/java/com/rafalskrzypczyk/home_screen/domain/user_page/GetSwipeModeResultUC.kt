package com.rafalskrzypczyk.home_screen.domain.user_page

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.home_screen.domain.CalculateResultUC
import com.rafalskrzypczyk.score.domain.ScoreManager
import com.rafalskrzypczyk.swipe_mode.domain.SwipeModeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSwipeModeResultUC @Inject constructor(
    private val scoreManager: ScoreManager,
    private val swipeModeRepository: SwipeModeRepository,
    private val calculateResult: CalculateResultUC
) {
    operator fun invoke(): Flow<Response<Int>> {
        val score = scoreManager.getScore()

        return swipeModeRepository.getSwipeQuestions().map { response ->
            when(response) {
                is Response.Loading -> Response.Loading
                is Response.Error -> Response.Error(response.error)
                is Response.Success -> {
                    val questions = response.data
                    val filteredScore =
                        score.seenQuestions.filter { it.questionId in questions.map { it.id } }
                    val result = calculateResult(filteredScore)
                    Response.Success(result)
                }
            }
        }
    }
}