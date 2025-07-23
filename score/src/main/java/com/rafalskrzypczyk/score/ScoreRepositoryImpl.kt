package com.rafalskrzypczyk.score

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.score.domain.Score
import com.rafalskrzypczyk.score.domain.ScoreRepository
import com.rafalskrzypczyk.score.domain.toDTO
import com.rafalskrzypczyk.score.domain.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ScoreRepositoryImpl @Inject constructor(
    private val firestore: FirestoreApi
) : ScoreRepository {
    override fun getUserScore(userId: String): Flow<Response<Score>> = firestore.getUserScore(userId).map {
        when(it) {
            is Response.Success -> Response.Success(it.data.toDomain())
            is Response.Error -> Response.Error(it.error)
            Response.Loading -> Response.Loading
        }
    }

    override fun saveUserScore(
        userId: String,
        score: Score,
    ): Flow<Response<Unit>> = firestore.updateUserScore(userId, score.toDTO())
}