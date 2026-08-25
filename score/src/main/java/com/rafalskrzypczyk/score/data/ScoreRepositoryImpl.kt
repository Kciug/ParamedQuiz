package com.rafalskrzypczyk.score.data

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.user_management.UserManager
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.score.domain.Score
import com.rafalskrzypczyk.score.domain.ScoreRepository
import com.rafalskrzypczyk.score.domain.ScoreStorage
import com.rafalskrzypczyk.score.domain.toDTO
import com.rafalskrzypczyk.score.domain.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Tozsamosc uzytkownika jest rozwiazywana w momencie kolekcji, nie budowy strumienia.
 * Odczyt przy budowie oznaczalby, ze operacja wykonana po wyczyszczeniu sesji trafia
 * do bufora goscia zamiast na konto.
 */
class ScoreRepositoryImpl @Inject constructor(
    private val firestore: FirestoreApi,
    private val userManager: UserManager,
    private val scoreStorage: ScoreStorage
) : ScoreRepository {
    override fun getUserScore(): Flow<Response<Score>> = flow {
        val user = userManager.getCurrentLoggedUser()

        if (user != null) {
            emitAll(
                firestore.getUserScore(user.id).map {
                    when (it) {
                        is Response.Success -> Response.Success(it.data.toDomain())
                        is Response.Error -> it
                        Response.Loading -> Response.Loading
                    }
                }
            )
        } else {
            emit(Response.Success(scoreStorage.getScore()))
        }
    }

    override fun saveUserScore(
        score: Score,
    ): Flow<Response<Unit>> = flow {
        val user = userManager.getCurrentLoggedUser()

        if (user != null) {
            emitAll(firestore.updateUserScore(user.id, score.toDTO()))
        } else {
            scoreStorage.saveScore(score)
            emit(Response.Success(Unit))
        }
    }

    override fun clearLocalScoreData() {
        scoreStorage.clearScore()
    }

    override fun deleteUserScore(): Flow<Response<Unit>> = flow {
        val user = userManager.getCurrentLoggedUser()

        if (user != null) {
            emitAll(firestore.deleteUserScore(user.id))
        } else {
            scoreStorage.clearScore()
            emit(Response.Success(Unit))
        }
    }
}
