package com.rafalskrzypczyk.score.domain

import com.rafalskrzypczyk.core.api_response.Response
import kotlinx.coroutines.flow.Flow

interface ScoreRepository {
    fun getUserScore(): Flow<Response<Score>>
    fun saveUserScore(score: Score): Flow<Response<Unit>>
    fun clearLocalScoreData()
    fun deleteUserScore(): Flow<Response<Unit>>
}