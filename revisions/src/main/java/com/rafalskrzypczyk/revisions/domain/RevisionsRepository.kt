package com.rafalskrzypczyk.revisions.domain

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.revisions.domain.models.RevisionCategory
import com.rafalskrzypczyk.revisions.domain.models.RevisionQuestion
import kotlinx.coroutines.flow.Flow

interface RevisionsRepository {
    fun getCategories(mode: QuizMode): Flow<Response<List<RevisionCategory>>>
    fun getQuestions(mode: QuizMode, categoryId: Long?): Flow<Response<List<RevisionQuestion>>>
}
