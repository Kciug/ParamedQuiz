package com.rafalskrzypczyk.firestore.data

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.error.AppError
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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val ORIGIN_GET_USER_DATA = "FirestoreService.getUserData"
private const val ORIGIN_UPDATE_USER_DATA = "FirestoreService.updateUserData"
private const val ORIGIN_DELETE_USER_DATA = "FirestoreService.deleteUserData"
private const val ORIGIN_GET_QUIZ_CATEGORIES = "FirestoreService.getQuizCategories"
private const val ORIGIN_GET_QUIZ_QUESTIONS = "FirestoreService.getQuizQuestions"
private const val ORIGIN_GET_SWIPE_QUESTIONS = "FirestoreService.getSwipeQuestions"
private const val ORIGIN_GET_TRANSLATION_QUESTIONS = "FirestoreService.getTranslationQuestions"
private const val ORIGIN_GET_CEM_CATEGORIES = "FirestoreService.getCemCategories"
private const val ORIGIN_GET_CEM_QUESTIONS = "FirestoreService.getCemQuestions"
private const val ORIGIN_GET_USER_SCORE = "FirestoreService.getUserScore"
private const val ORIGIN_UPDATE_USER_SCORE = "FirestoreService.updateUserScore"
private const val ORIGIN_DELETE_USER_SCORE = "FirestoreService.deleteUserScore"
private const val ORIGIN_SEND_ISSUE_REPORT = "FirestoreService.sendIssueReport"
private const val ORIGIN_SAVE_FEEDBACK = "FirestoreService.saveFeedback"
private const val ORIGIN_GET_TERMS_OF_SERVICE = "FirestoreService.getTermsOfService"
private const val ORIGIN_GET_TERMS_OF_SERVICE_UPDATES = "FirestoreService.getTermsOfServiceUpdates"
private const val ORIGIN_GET_QUESTIONS_COUNT_UPDATES = "FirestoreService.getQuestionsCountUpdates"
private const val ORIGIN_GET_NEWS_BANNERS = "FirestoreService.getNewsBanners"
private const val ORIGIN_GET_NOTIFICATION_CONFIG = "FirestoreService.getNotificationConfig"
private const val ORIGIN_GET_NOTIFICATION_TEMPLATES = "FirestoreService.getNotificationTemplates"

class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val errorMapper: FirestoreErrorMapper
) : FirestoreApi {
    override fun getUserData(userId: String): Flow<Response<UserDataDTO>> = flow {
        emit(Response.Loading)
        val result = getFirestoreDocumentData(FirestoreCollections.USER_DATA_COLLECTION, userId)?.toObject(UserDataDTO::class.java)
        emit(result?.let { Response.Success(it) } ?: Response.Error(errorMapper.report(ORIGIN_GET_USER_DATA, AppError.Data.NoData)))
    }.mapErrors(ORIGIN_GET_USER_DATA)

    override fun updateUserData(userData: UserDataDTO): Flow<Response<Unit>> = flow {
        emit(Response.Loading)
        emit(modifyFirestoreDocument(userData.id, userData, FirestoreCollections.USER_DATA_COLLECTION, ORIGIN_UPDATE_USER_DATA))
    }

    override fun deleteUserData(userId: String): Flow<Response<Unit>> = flow {
        emit(Response.Loading)
        emit(deleteFirestoreDocument(userId, FirestoreCollections.USER_DATA_COLLECTION, ORIGIN_DELETE_USER_DATA))
    }

    override fun getQuizCategories(): Flow<Response<List<CategoryDTO>>> = flow {
        emit(Response.Loading)
        val questions = getFirestoreData(FirestoreCollections.QUIZ_MODE_CATEGORIES)?.toObjects(CategoryDTO::class.java) ?: emptyList()
        emit(Response.Success(questions))
    }.mapErrors(ORIGIN_GET_QUIZ_CATEGORIES)

    override fun getQuizQuestions(): Flow<Response<List<QuestionDTO>>> = flow {
        emit(Response.Loading)
        val questions = getFirestoreData(FirestoreCollections.QUIZ_MODE_QUESTIONS)?.toObjects(QuestionDTO::class.java) ?: emptyList()
        emit(Response.Success(questions))
    }.mapErrors(ORIGIN_GET_QUIZ_QUESTIONS)

    override fun getUpdatedCategories(): Flow<List<CategoryDTO>> = attachFirestoreListener(FirestoreCollections.QUIZ_MODE_CATEGORIES)
        .map { it.toObjects(CategoryDTO::class.java) }

    override fun getUpdatedQuestions(): Flow<List<QuestionDTO>> = attachFirestoreListener(FirestoreCollections.QUIZ_MODE_QUESTIONS)
        .map { it.toObjects(QuestionDTO::class.java) }

    override fun getSwipeQuestions(): Flow<Response<List<SwipeQuestionDTO>>> = flow {
        emit(Response.Loading)
        val questions = getFirestoreData(FirestoreCollections.SWIPE_QUESTIONS)?.toObjects(SwipeQuestionDTO::class.java) ?: emptyList()
        emit(Response.Success(questions))
    }.mapErrors(ORIGIN_GET_SWIPE_QUESTIONS)

    override fun getUpdatedSwipeQuestions(): Flow<List<SwipeQuestionDTO>> = attachFirestoreListener(FirestoreCollections.SWIPE_QUESTIONS)
        .map { it.toObjects(SwipeQuestionDTO::class.java) }

    override fun getTranslationQuestions(): Flow<Response<List<TranslationQuestionDTO>>> = flow {
        emit(Response.Loading)
        val questions = getFirestoreData(FirestoreCollections.TRANSLATION_QUESTIONS)?.toObjects(TranslationQuestionDTO::class.java) ?: emptyList()
        emit(Response.Success(questions))
    }.mapErrors(ORIGIN_GET_TRANSLATION_QUESTIONS)

    override fun getUpdatedTranslationQuestions(): Flow<List<TranslationQuestionDTO>> = attachFirestoreListener(FirestoreCollections.TRANSLATION_QUESTIONS)
        .map { it.toObjects(TranslationQuestionDTO::class.java) }

    override fun getCemCategories(): Flow<Response<List<CemCategoryDTO>>> = flow {
        emit(Response.Loading)
        val categories = getFirestoreData(FirestoreCollections.CEM_CATEGORIES)?.toObjects(CemCategoryDTO::class.java) ?: emptyList()
        emit(Response.Success(categories))
    }.mapErrors(ORIGIN_GET_CEM_CATEGORIES)

    override fun getUpdatedCemCategories(): Flow<List<CemCategoryDTO>> = attachFirestoreListener(FirestoreCollections.CEM_CATEGORIES)
        .map { it.toObjects(CemCategoryDTO::class.java) }

    override fun getCemQuestions(): Flow<Response<List<QuestionDTO>>> = flow {
        emit(Response.Loading)
        val questions = getFirestoreData(FirestoreCollections.CEM_QUESTIONS)?.toObjects(QuestionDTO::class.java) ?: emptyList()
        emit(Response.Success(questions))
    }.mapErrors(ORIGIN_GET_CEM_QUESTIONS)

    override fun getUpdatedCemQuestions(): Flow<List<QuestionDTO>> = attachFirestoreListener(FirestoreCollections.CEM_QUESTIONS)
    .map { it.toObjects(QuestionDTO::class.java) }

    override fun getUserScore(userId: String): Flow<Response<ScoreDTO>> = flow {
        emit(Response.Loading)
        val result = firestore.collection(FirestoreCollections.USER_SCORE).document(userId)
            .get()
            .await()
            .toObject(ScoreDTO::class.java)
        emit(result?.let { Response.Success(it) } ?: Response.Success(ScoreDTO()))
    }.mapErrors(ORIGIN_GET_USER_SCORE)

    override fun updateUserScore(
        userId: String,
        score: ScoreDTO,
    ): Flow<Response<Unit>> = flow {
        emit(Response.Loading)
        emit(modifyFirestoreDocument(userId, score, FirestoreCollections.USER_SCORE, ORIGIN_UPDATE_USER_SCORE))
    }

    override fun deleteUserScore(userId: String): Flow<Response<Unit>> = flow {
        emit(Response.Loading)
        emit(deleteFirestoreDocument(userId, FirestoreCollections.USER_SCORE, ORIGIN_DELETE_USER_SCORE))
    }

    override fun sendIssueReport(report: IssueReportDTO): Flow<Response<Unit>> = flow {
        emit(Response.Loading)
        val docId = firestore.collection(FirestoreCollections.ISSUES_REPORTS).document().id
        val reportWithId = report.copy(id = docId)
        emit(modifyFirestoreDocument(docId, reportWithId, FirestoreCollections.ISSUES_REPORTS, ORIGIN_SEND_ISSUE_REPORT))
    }

    override fun saveFeedback(feedback: FeedbackDTO): Flow<Response<Unit>> = flow {
        emit(Response.Loading)
        val docId = firestore.collection(FirestoreCollections.APP_FEEDBACK).document().id
        val feedbackWithId = feedback.copy(id = docId)
        emit(modifyFirestoreDocument(docId, feedbackWithId, FirestoreCollections.APP_FEEDBACK, ORIGIN_SAVE_FEEDBACK))
    }

    override fun getTermsOfService(): Flow<Response<TermsOfServiceDTO>> = flow {
        emit(Response.Loading)
        val snapshot = getFirestoreDocumentData(FirestoreCollections.APP_CONFIG, FirestoreCollections.TERMS_OF_SERVICE)
        val terms = snapshot?.toObject(TermsOfServiceDTO::class.java)
        emit(terms?.let { Response.Success(it) } ?: Response.Error(errorMapper.report(ORIGIN_GET_TERMS_OF_SERVICE, AppError.Data.NoData)))
    }.mapErrors(ORIGIN_GET_TERMS_OF_SERVICE)

    override fun getTermsOfServiceUpdates(): Flow<Response<TermsOfServiceDTO>> = attachFirestoreDocumentListener(
        collection = FirestoreCollections.APP_CONFIG,
        documentId = FirestoreCollections.TERMS_OF_SERVICE
    ).map { snapshot ->
        val terms = snapshot.toObject(TermsOfServiceDTO::class.java)
        if (terms != null) {
            Response.Success(terms)
        } else {
            Response.Error(errorMapper.report(ORIGIN_GET_TERMS_OF_SERVICE_UPDATES, AppError.Data.NoData))
        }
    }.mapErrors(ORIGIN_GET_TERMS_OF_SERVICE_UPDATES)

    override fun getQuestionsCountUpdates(collection: String): Flow<Int> = flow {
        val countQuery = firestore.collection(collection).count()
        val snapshot = countQuery.get(com.google.firebase.firestore.AggregateSource.SERVER).await()
        emit(snapshot.count.toInt())
    }.catch {
        errorMapper.toAppError(ORIGIN_GET_QUESTIONS_COUNT_UPDATES, it)
        emit(0)
    }

    override fun getNewsBanners(): Flow<Response<List<NewsBannerDTO>>> = flow {
        emit(Response.Loading)
        val banners = getFirestoreData(FirestoreCollections.NEWS_BANNERS)
            ?.toObjects(NewsBannerDTO::class.java)
            ?.filter { it.isActive } ?: emptyList()
        emit(Response.Success(banners))
    }.mapErrors(ORIGIN_GET_NEWS_BANNERS)

    override fun getNewsBannerUpdates(): Flow<List<NewsBannerDTO>> = attachFirestoreListener(FirestoreCollections.NEWS_BANNERS)
        .map { it.toObjects(NewsBannerDTO::class.java).filter { banner -> banner.isActive } }

    override fun getNotificationConfig(): Flow<Response<NotificationConfigDTO>> = flow {
        emit(Response.Loading)
        val config = firestore.collection(FirestoreCollections.APP_CONFIG)
            .document(FirestoreCollections.NOTIFICATIONS_CONFIG)
            .get()
            .await()
            .toObject(NotificationConfigDTO::class.java)
        emit(config?.let { Response.Success(it) } ?: Response.Error(errorMapper.report(ORIGIN_GET_NOTIFICATION_CONFIG, AppError.Data.NoData)))
    }.mapErrors(ORIGIN_GET_NOTIFICATION_CONFIG)

    override fun getNotificationTemplates(): Flow<Response<List<NotificationTemplateDTO>>> = flow {
        emit(Response.Loading)
        val templates = firestore.collection(FirestoreCollections.NOTIFICATION_TEMPLATES)
            .get()
            .await()
            .toObjects(NotificationTemplateDTO::class.java)
        emit(Response.Success(templates))
    }.mapErrors(ORIGIN_GET_NOTIFICATION_TEMPLATES)

    private fun <T> Flow<Response<T>>.mapErrors(origin: String): Flow<Response<T>> =
        catch { emit(Response.Error(errorMapper.toAppError(origin, it))) }

    private suspend fun getFirestoreDocumentData(collection: String, documentId: String): DocumentSnapshot? {
        return try {
            firestore.collection(collection).document(documentId)
                .get(Source.CACHE)
                .await()
                .takeIf { it.exists() }
        } catch (_: Exception) {
            null
        } ?: firestore.collection(collection).document(documentId).get(Source.SERVER).await()
    }

    private fun attachFirestoreDocumentListener(collection: String, documentId: String): Flow<DocumentSnapshot> = callbackFlow {
        val listener = firestore.collection(collection).document(documentId).addSnapshotListener { value, error ->
            if (value?.metadata?.isFromCache == true) return@addSnapshotListener
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            value?.let { trySend(it) }
        }
        awaitClose { listener.remove() }
    }

    private suspend fun getFirestoreData(collection: String): QuerySnapshot? {
        return firestore.collection(collection)
            .get(Source.CACHE)
            .await()
            .takeIf { it.isEmpty.not() }
            ?: firestore.collection(collection).get(Source.SERVER).await()
    }

    private fun attachFirestoreListener(collection: String): Flow<QuerySnapshot> = callbackFlow {
        val listener = firestore.collection(collection).addSnapshotListener { value, error ->
            if(value?.metadata?.isFromCache == true) return@addSnapshotListener
            if(error != null) {
                close(error)
                return@addSnapshotListener
            }
            value?.let { trySend(it) }
        }
        awaitClose { listener.remove() }
    }

    private suspend fun <T : Any> modifyFirestoreDocument(
        id: String,
        data: T,
        collection: String,
        origin: String,
    ): Response<Unit> {
        return try {
            firestore.collection(collection)
                .document(id)
                .set(data, SetOptions.merge())
                .await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(errorMapper.toAppError(origin, e))
        }
    }

    private suspend fun deleteFirestoreDocument(
        id: String,
        collection: String,
        origin: String,
    ): Response<Unit> {
        return try {
            firestore.collection(collection)
                .document(id)
                .delete()
                .await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(errorMapper.toAppError(origin, e))
        }
    }
}
