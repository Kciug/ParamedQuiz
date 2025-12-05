package com.rafalskrzypczyk.main_mode.data

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.main_mode.domain.MainModeRepository
import com.rafalskrzypczyk.main_mode.domain.models.Category
import com.rafalskrzypczyk.main_mode.domain.models.Question
import com.rafalskrzypczyk.main_mode.domain.models.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MainModeRepositoryImpl @Inject constructor(
    private val firestore: FirestoreApi
): MainModeRepository {
    private var categories: List<Category>? = null
    private var questions: List<Question>? = null

    override fun getAllCategories(): Flow<Response<List<Category>>> = flow {
        if (!categories.isNullOrEmpty()) {
            emit(Response.Success(categories!!))
        } else {
            emitAll(
                firestore.getQuizCategories().map {
                    when (it) {
                        is Response.Success -> {
                            categories = it.data.map { dto -> dto.toDomain() }
                            Response.Success(categories!!)
                        }
                        is Response.Error -> Response.Error(it.error)
                        is Response.Loading -> Response.Loading
                    }
                }
            )
        }
    }.flowOn(Dispatchers.Default)


    override fun getAllQuestions(): Flow<Response<List<Question>>> = flow {
        if (!questions.isNullOrEmpty()) {
            emit(Response.Success(questions!!))
        } else {
            emitAll(
                firestore.getQuizQuestions().map {
                    when (it) {
                        is Response.Success -> {
                            questions = it.data.map { dto -> dto.toDomain() }
                            Response.Success(questions!!)
                        }
                        is Response.Error -> Response.Error(it.error)
                        is Response.Loading -> Response.Loading
                    }
                }
            )
        }
    }.flowOn(Dispatchers.Default)


    override fun getUpdatedCategories(): Flow<List<Category>> = firestore.getUpdatedCategories().map {
        categories = it.map { dto -> dto.toDomain() }
        categories!!
    }.flowOn(Dispatchers.Default)

    override fun getUpdatedQuestions(): Flow<List<Question>> = firestore.getUpdatedQuestions().map {
        questions = it.map { dto -> dto.toDomain() }
        questions!!
    }.flowOn(Dispatchers.Default)
}