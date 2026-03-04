package com.rafalskrzypczyk.cem_mode.domain

import com.rafalskrzypczyk.cem_mode.domain.models.CemCategory
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.main_mode.domain.models.Question
import kotlinx.coroutines.flow.Flow

interface CemRepository {
    fun getCemCategories(): Flow<Response<List<CemCategory>>>
    fun getUpdatedCemCategories(): Flow<List<CemCategory>>
    fun getAllCemQuestions(): Flow<Response<List<Question>>>
    fun getUpdatedCemQuestions(): Flow<List<Question>>
}
