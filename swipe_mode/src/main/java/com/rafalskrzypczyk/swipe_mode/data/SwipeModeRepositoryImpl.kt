package com.rafalskrzypczyk.swipe_mode.data

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.swipe_mode.domain.SwipeModeRepository
import com.rafalskrzypczyk.swipe_mode.domain.SwipeQuestion
import com.rafalskrzypczyk.swipe_mode.domain.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SwipeModeRepositoryImpl @Inject constructor(
    private val firestore: FirestoreApi
): SwipeModeRepository {
    private var swipeQuestions: List<SwipeQuestion>? = null

    override fun getSwipeQuestions(): Flow<Response<List<SwipeQuestion>>> = flow {
        val cached = swipeQuestions
        if (!cached.isNullOrEmpty()) {
            emit(Response.Success(cached))
        } else {
            emitAll(
                firestore.getSwipeQuestions().map {
                    when (it) {
                        is Response.Success -> {
                            val domainQuestions = it.data.map { dto -> dto.toDomain() }
                            swipeQuestions = domainQuestions
                            Response.Success(domainQuestions)
                        }
                        is Response.Error -> Response.Error(it.error)
                        is Response.Loading -> Response.Loading
                    }
                }
            )
        }
    }.flowOn(Dispatchers.Default)

    override fun getUpdatedQuestions(isTrial: Boolean): Flow<List<SwipeQuestion>> = firestore.getUpdatedSwipeQuestions().map { questions ->
        val domainQuestions = questions.map { dto -> dto.toDomain() }
        swipeQuestions = domainQuestions
        if (isTrial) domainQuestions.filter { it.isFree } else domainQuestions
    }.flowOn(Dispatchers.Default)
}
