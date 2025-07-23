package com.rafalskrzypczyk.score.domain

import com.rafalskrzypczyk.core.api_response.Response
import kotlinx.coroutines.flow.Flow

interface ScoreRepository {
    fun getUserScore(userId: String): Flow<Response<Score>>
    fun saveUserScore(userId: String, score: Score): Flow<Response<Unit>>
}