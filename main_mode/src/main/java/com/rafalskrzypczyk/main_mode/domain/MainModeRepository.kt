package com.rafalskrzypczyk.main_mode.domain

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.main_mode.domain.models.Category
import com.rafalskrzypczyk.main_mode.domain.models.Question
import kotlinx.coroutines.flow.Flow

interface MainModeRepository {
    fun getAllCategories(): Flow<Response<List<Category>>>
    fun getAllQuestions(): Flow<Response<List<Question>>>
    fun getUpdatedCategories(): Flow<List<Category>>
    fun getUpdatedQuestions(): Flow<List<Question>>
}