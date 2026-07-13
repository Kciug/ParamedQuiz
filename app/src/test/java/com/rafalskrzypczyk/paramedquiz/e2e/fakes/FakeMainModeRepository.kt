package com.rafalskrzypczyk.paramedquiz.e2e.fakes

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.main_mode.domain.MainModeRepository
import com.rafalskrzypczyk.main_mode.domain.models.Category
import com.rafalskrzypczyk.main_mode.domain.models.Question
import com.rafalskrzypczyk.main_mode.domain.models.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Wariant repozytorium trybu głównego **bez cache** — czyta [FakeFirestoreApi] na świeżo przy każdym
 * wywołaniu. Produkcyjny `MainModeRepositoryImpl` cache'uje pola, a Robolectric współdzieli singletony
 * między testami → stale cache psuje kolejne testy. Ten fake to eliminuje.
 */
class FakeMainModeRepository @Inject constructor(
    private val firestore: FirestoreApi
) : MainModeRepository {

    override fun getAllCategories(): Flow<Response<List<Category>>> =
        firestore.getQuizCategories().map { resp ->
            when (resp) {
                is Response.Success -> Response.Success(resp.data.map { it.toDomain() })
                is Response.Error -> Response.Error(resp.error)
                Response.Loading -> Response.Loading
            }
        }

    override fun getAllQuestions(): Flow<Response<List<Question>>> =
        firestore.getQuizQuestions().map { resp ->
            when (resp) {
                is Response.Success -> Response.Success(resp.data.map { it.toDomain() })
                is Response.Error -> Response.Error(resp.error)
                Response.Loading -> Response.Loading
            }
        }

    override fun getUpdatedCategories(): Flow<List<Category>> =
        firestore.getUpdatedCategories().map { list -> list.map { it.toDomain() } }

    override fun getUpdatedQuestions(): Flow<List<Question>> =
        firestore.getUpdatedQuestions().map { list -> list.map { it.toDomain() } }
}
