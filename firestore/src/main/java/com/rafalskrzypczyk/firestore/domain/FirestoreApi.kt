package com.rafalskrzypczyk.firestore.domain

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.firestore.domain.models.CategoryDTO
import com.rafalskrzypczyk.firestore.domain.models.IssueReportDTO
import com.rafalskrzypczyk.firestore.domain.models.QuestionDTO
import com.rafalskrzypczyk.firestore.domain.models.ScoreDTO
import com.rafalskrzypczyk.firestore.domain.models.SwipeQuestionDTO
import com.rafalskrzypczyk.firestore.domain.models.TranslationQuestionDTO
import com.rafalskrzypczyk.firestore.domain.models.UserDataDTO
import kotlinx.coroutines.flow.Flow

interface FirestoreApi {
    fun getUserData(userId: String) : Flow<Response<UserDataDTO>>
    fun updateUserData(userData: UserDataDTO) : Flow<Response<Unit>>
    fun deleteUserData(userId: String) : Flow<Response<Unit>>

    fun getQuizCategories() : Flow<Response<List<CategoryDTO>>>
    fun getQuizQuestions() : Flow<Response<List<QuestionDTO>>>
    fun getUpdatedCategories() : Flow<List<CategoryDTO>>
    fun getUpdatedQuestions() : Flow<List<QuestionDTO>>

        fun getSwipeQuestions(): Flow<Response<List<SwipeQuestionDTO>>>

        fun getUpdatedSwipeQuestions(): Flow<List<SwipeQuestionDTO>>

    

            fun getTranslationQuestions(): Flow<Response<List<TranslationQuestionDTO>>>

    

            fun getUpdatedTranslationQuestions(): Flow<List<TranslationQuestionDTO>>

    

        

    

            fun getUserScore(userId: String): Flow<Response<ScoreDTO>>
    fun updateUserScore(userId: String, score: ScoreDTO) : Flow<Response<Unit>>
    fun deleteUserScore(userId: String) : Flow<Response<Unit>>

    fun sendIssueReport(report: IssueReportDTO) : Flow<Response<Unit>>
}