package com.rafalskrzypczyk.cem_mode.data

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.cem_mode.domain.CemRepository
import com.rafalskrzypczyk.cem_mode.domain.models.CemCategory
import com.rafalskrzypczyk.cem_mode.domain.models.toDomain
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CemRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi
) : CemRepository {

    override fun getCemCategories(): Flow<Response<List<CemCategory>>> =
        firestoreApi.getCemCategories().map { response ->
            when (response) {
                is Response.Success -> Response.Success(response.data.map { it.toDomain() })
                is Response.Error -> Response.Error(response.error)
                Response.Loading -> Response.Loading
            }
        }

    override fun getUpdatedCemCategories(): Flow<List<CemCategory>> =
        firestoreApi.getUpdatedCemCategories().map { list ->
            list.map { it.toDomain() }
        }
}
