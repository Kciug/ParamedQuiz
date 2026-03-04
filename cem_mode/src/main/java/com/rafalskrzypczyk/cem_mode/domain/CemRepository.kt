package com.rafalskrzypczyk.cem_mode.domain

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.cem_mode.domain.models.CemCategory
import kotlinx.coroutines.flow.Flow

interface CemRepository {
    fun getCemCategories(): Flow<Response<List<CemCategory>>>
    fun getUpdatedCemCategories(): Flow<List<CemCategory>>
}
