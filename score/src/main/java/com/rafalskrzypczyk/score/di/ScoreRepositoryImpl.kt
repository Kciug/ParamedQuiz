package com.rafalskrzypczyk.score.di

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.user_management.UserManager
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.score.domain.ScoreStorage
import com.rafalskrzypczyk.score.domain.Score
import com.rafalskrzypczyk.score.domain.ScoreRepository
import com.rafalskrzypczyk.score.domain.toDTO
import com.rafalskrzypczyk.score.domain.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ScoreRepositoryImpl @Inject constructor(
    private val firestore: FirestoreApi,
    private val userManager: UserManager,
    private val scoreStorage: ScoreStorage
) : ScoreRepository {
    override fun getUserScore(): Flow<Response<Score>> {
        val user = userManager.getCurrentLoggedUser()

        return if(user != null) {
            firestore.getUserScore(user.id).map {
                when (it) {
                    is Response.Success -> Response.Success(it.data.toDomain())
                    is Response.Error -> Response.Error(it.error)
                    Response.Loading -> Response.Loading
                }
            }
        } else {
            flow {
                emit(Response.Success(scoreStorage.getScore()))
            }
        }
    }

    override fun saveUserScore(
        score: Score,
    ): Flow<Response<Unit>> {
        val user = userManager.getCurrentLoggedUser()

        return if(user != null) {
            firestore.updateUserScore(user.id, score.toDTO())
        } else {
            flow {
                scoreStorage.saveScore(score)
                emit(Response.Success(Unit))
            }
        }
    }

    override fun clearLocalScoreData() {
        scoreStorage.clearScore()
    }

    override fun deleteUserScore(): Flow<Response<Unit>> {
        val user = userManager.getCurrentLoggedUser()

        return if(user != null) {
            firestore.deleteUserScore(user.id)
        } else {
            flow { emit(Response.Error("User not found")) }
        }
    }
}