package com.rafalskrzypczyk.paramedquiz.e2e.fakes

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.firestore.domain.models.CategoryDTO
import com.rafalskrzypczyk.firestore.domain.models.CemCategoryDTO
import com.rafalskrzypczyk.firestore.domain.models.FeedbackDTO
import com.rafalskrzypczyk.firestore.domain.models.IssueReportDTO
import com.rafalskrzypczyk.firestore.domain.models.NewsBannerDTO
import com.rafalskrzypczyk.firestore.domain.models.NotificationConfigDTO
import com.rafalskrzypczyk.firestore.domain.models.NotificationTemplateDTO
import com.rafalskrzypczyk.firestore.domain.models.QuestionDTO
import com.rafalskrzypczyk.firestore.domain.models.ScoreDTO
import com.rafalskrzypczyk.firestore.domain.models.SwipeQuestionDTO
import com.rafalskrzypczyk.firestore.domain.models.TermsOfServiceDTO
import com.rafalskrzypczyk.firestore.domain.models.TranslationQuestionDTO
import com.rafalskrzypczyk.firestore.domain.models.UserDataDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

/**
 * In-memory fake backendu dla testów E2E (Robolectric + Hilt). Zastępuje `FirestoreApi` bez Firebase.
 * Dane są **zaseedowane** i mutowalne przez pola — kolejne testy mogą je nadpisać przed uruchomieniem.
 *
 * Domyślne wartości pokrywają scenariusz harnessu (regulamin). Pozostałe kolekcje są puste i będą
 * rozszerzane wraz z kolejnymi scenariuszami (Porcja 3).
 */
class FakeFirestoreApi : FirestoreApi {

    var termsOfService: TermsOfServiceDTO = TermsOfServiceDTO(
        version = 1,
        content = "REGULAMIN_TESTOWY",
        isMandatory = true
    )

    var quizCategories: List<CategoryDTO> = emptyList()
    var quizQuestions: List<QuestionDTO> = emptyList()
    var swipeQuestions: List<SwipeQuestionDTO> = emptyList()
    var translationQuestions: List<TranslationQuestionDTO> = emptyList()
    var cemCategories: List<CemCategoryDTO> = emptyList()
    var cemQuestions: List<QuestionDTO> = emptyList()
    var newsBanners: List<NewsBannerDTO> = emptyList()

    override fun getUserData(userId: String): Flow<Response<UserDataDTO>> = emptyFlow()
    override fun updateUserData(userData: UserDataDTO): Flow<Response<Unit>> = flowOf(Response.Success(Unit))
    override fun deleteUserData(userId: String): Flow<Response<Unit>> = flowOf(Response.Success(Unit))

    override fun getQuizCategories(): Flow<Response<List<CategoryDTO>>> = flowOf(Response.Success(quizCategories))
    override fun getQuizQuestions(): Flow<Response<List<QuestionDTO>>> = flowOf(Response.Success(quizQuestions))
    override fun getUpdatedCategories(): Flow<List<CategoryDTO>> = flowOf(quizCategories)
    override fun getUpdatedQuestions(): Flow<List<QuestionDTO>> = flowOf(quizQuestions)

    override fun getSwipeQuestions(): Flow<Response<List<SwipeQuestionDTO>>> = flowOf(Response.Success(swipeQuestions))
    override fun getUpdatedSwipeQuestions(): Flow<List<SwipeQuestionDTO>> = flowOf(swipeQuestions)

    override fun getTranslationQuestions(): Flow<Response<List<TranslationQuestionDTO>>> =
        flowOf(Response.Success(translationQuestions))
    override fun getUpdatedTranslationQuestions(): Flow<List<TranslationQuestionDTO>> = flowOf(translationQuestions)

    override fun getCemCategories(): Flow<Response<List<CemCategoryDTO>>> = flowOf(Response.Success(cemCategories))
    override fun getUpdatedCemCategories(): Flow<List<CemCategoryDTO>> = flowOf(cemCategories)
    override fun getCemQuestions(): Flow<Response<List<QuestionDTO>>> = flowOf(Response.Success(cemQuestions))
    override fun getUpdatedCemQuestions(): Flow<List<QuestionDTO>> = flowOf(cemQuestions)

    override fun getUserScore(userId: String): Flow<Response<ScoreDTO>> = emptyFlow()
    override fun updateUserScore(userId: String, score: ScoreDTO): Flow<Response<Unit>> = flowOf(Response.Success(Unit))
    override fun deleteUserScore(userId: String): Flow<Response<Unit>> = flowOf(Response.Success(Unit))

    override fun sendIssueReport(report: IssueReportDTO): Flow<Response<Unit>> = flowOf(Response.Success(Unit))
    override fun saveFeedback(feedback: FeedbackDTO): Flow<Response<Unit>> = flowOf(Response.Success(Unit))

    override fun getTermsOfService(): Flow<Response<TermsOfServiceDTO>> = flowOf(Response.Success(termsOfService))
    override fun getTermsOfServiceUpdates(): Flow<Response<TermsOfServiceDTO>> = flowOf(Response.Success(termsOfService))

    override fun getQuestionsCountUpdates(collection: String): Flow<Int> = flowOf(0)

    override fun getNewsBanners(): Flow<Response<List<NewsBannerDTO>>> = flowOf(Response.Success(newsBanners))
    override fun getNewsBannerUpdates(): Flow<List<NewsBannerDTO>> = flowOf(newsBanners)

    override fun getNotificationConfig(): Flow<Response<NotificationConfigDTO>> = emptyFlow()
    override fun getNotificationTemplates(): Flow<Response<List<NotificationTemplateDTO>>> =
        flowOf(Response.Success(emptyList()))
}
