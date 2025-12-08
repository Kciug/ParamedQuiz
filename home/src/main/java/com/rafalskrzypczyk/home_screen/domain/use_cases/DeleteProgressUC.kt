package com.rafalskrzypczyk.home_screen.domain.use_cases

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.score.domain.ScoreManager
import com.rafalskrzypczyk.score.domain.ScoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class DeleteProgressUC @Inject constructor(
    private val scoreRepository: ScoreRepository,
    private val scoreManager: ScoreManager
) {
    operator fun invoke(): Flow<Response<Unit>> {
        return scoreRepository.deleteUserScore().onEach { 
            if (it is Response.Success) {
                scoreManager.clearScore()
            }
        }
    }
}