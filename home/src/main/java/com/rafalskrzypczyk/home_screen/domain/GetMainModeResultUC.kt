package com.rafalskrzypczyk.home_screen.domain

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.main_mode.domain.MainModeRepository
import com.rafalskrzypczyk.score.ScoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMainModeResultUC @Inject constructor(
    private val scoreManager: ScoreManager,
    private val mainModeRepository: MainModeRepository,
    private val calculateResult: CalculateResultUC
) {
    operator fun invoke(): Flow<Response<Int>> {
        val score = scoreManager.getScore()

        return mainModeRepository.getAllQuestions().map { response ->
            when(response) {
                is Response.Loading -> Response.Loading
                is Response.Error -> Response.Error(response.error)
                is Response.Success -> {
                    val questions = response.data
                    val filteredScore = score.seenQuestions.filter { it.questionId in questions.map { it.id } }
                    val result = calculateResult(filteredScore)
                    Response.Success(result)
                }
            }
        }
    }
}