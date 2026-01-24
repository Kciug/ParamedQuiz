package com.rafalskrzypczyk.firestore.data

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.firestore.domain.models.CategoryDTO
import com.rafalskrzypczyk.firestore.domain.models.IssueReportDTO
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

class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val resourceProvider: ResourceProvider
) : FirestoreApi {
    override fun getUserData(userId: String): Flow<Response<UserDataDTO>> = flow {
        emit(Response.Loading)
        val result = firestore.collection(FirestoreCollections.USER_DATA_COLLECTION)
            .document(userId)
            .get()
            .await()
            .toObject(UserDataDTO::class.java)

        emit(result?.let { Response.Success(it) } ?: Response.Error(resourceProvider.getString(R.string.error_no_data)))
    }.catch { emit(Response.Error(it.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))) }

    override fun updateUserData(userData: UserDataDTO): Flow<Response<Unit>> = flow {
        emit(Response.Loading)
        emit(modifyFirestoreDocument(userData.id, userData, FirestoreCollections.USER_DATA_COLLECTION))
    }

    override fun deleteUserData(userId: String): Flow<Response<Unit>> = flow {
        emit(Response.Loading)
        emit(deleteFirestoreDocument(userId, FirestoreCollections.USER_DATA_COLLECTION))
    }

    override fun getQuizCategories(): Flow<Response<List<CategoryDTO>>> = flow {
        emit(Response.Loading)
        val questions = getFirestoreData(FirestoreCollections.QUIZ_MODE_CATEGORIES)?.toObjects(CategoryDTO::class.java) ?: emptyList()
        emit(Response.Success(questions))
    }.catch { emit(Response.Error(it.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))) }

    override fun getQuizQuestions(): Flow<Response<List<QuestionDTO>>> = flow {
        emit(Response.Loading)
        val questions = getFirestoreData(FirestoreCollections.QUIZ_MODE_QUESTIONS)?.toObjects(QuestionDTO::class.java) ?: emptyList()
        emit(Response.Success(questions))
    }.catch { emit(Response.Error(it.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))) }

    override fun getUpdatedCategories(): Flow<List<CategoryDTO>> = attachFirestoreListener(FirestoreCollections.QUIZ_MODE_CATEGORIES)
        .map { it.toObjects(CategoryDTO::class.java) }

    override fun getUpdatedQuestions(): Flow<List<QuestionDTO>> = attachFirestoreListener(FirestoreCollections.QUIZ_MODE_QUESTIONS)
        .map { it.toObjects(QuestionDTO::class.java) }

    override fun getSwipeQuestions(): Flow<Response<List<SwipeQuestionDTO>>> = flow {
        emit(Response.Loading)
        val questions = getFirestoreData(FirestoreCollections.SWIPE_QUESTIONS)?.toObjects(SwipeQuestionDTO::class.java) ?: emptyList()
        emit(Response.Success(questions))
    }.catch { emit(Response.Error(it.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))) }

    override fun getUpdatedSwipeQuestions(): Flow<List<SwipeQuestionDTO>> = attachFirestoreListener(FirestoreCollections.SWIPE_QUESTIONS)
        .map { it.toObjects(SwipeQuestionDTO::class.java) }

    override fun getTranslationQuestions(): Flow<Response<List<TranslationQuestionDTO>>> = flow {
        emit(Response.Loading)
        val questions = getFirestoreData(FirestoreCollections.TRANSLATION_QUESTIONS)?.toObjects(TranslationQuestionDTO::class.java) ?: emptyList()
        emit(Response.Success(questions))
    }.catch { emit(Response.Error(it.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))) }

    override fun getUpdatedTranslationQuestions(): Flow<List<TranslationQuestionDTO>> = attachFirestoreListener(FirestoreCollections.TRANSLATION_QUESTIONS)
        .map { it.toObjects(TranslationQuestionDTO::class.java) }

    override fun getUserScore(userId: String): Flow<Response<ScoreDTO>> = flow {
        emit(Response.Loading)
        val result = firestore.collection(FirestoreCollections.USER_SCORE)
            .document(userId)
            .get()
            .await()
            .toObject(ScoreDTO::class.java)
        emit(result?.let { Response.Success(it) } ?: Response.Error(resourceProvider.getString(R.string.error_no_data)))
    }.catch { emit(Response.Error(it.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))) }

    override fun updateUserScore(
        userId: String,
        score: ScoreDTO,
    ): Flow<Response<Unit>> = flow {
        emit(Response.Loading)
        emit(modifyFirestoreDocument(userId, score, FirestoreCollections.USER_SCORE))
    }

    override fun deleteUserScore(userId: String): Flow<Response<Unit>> = flow {
        emit(Response.Loading)
        emit(deleteFirestoreDocument(userId, FirestoreCollections.USER_SCORE))
    }

    override fun sendIssueReport(report: IssueReportDTO): Flow<Response<Unit>> = flow {
        emit(Response.Loading)
        val docId = firestore.collection(FirestoreCollections.ISSUES_REPORTS).document().id
        val reportWithId = report.copy(id = docId)
        emit(modifyFirestoreDocument(docId, reportWithId, FirestoreCollections.ISSUES_REPORTS))
    }

    override fun getTermsOfService(): Flow<Response<TermsOfServiceDTO>> = flow {
        emit(Response.Loading)
        val snapshot = getFirestoreDocumentData(FirestoreCollections.APP_CONFIG, FirestoreCollections.TERMS_OF_SERVICE)
        val terms = snapshot?.toObject(TermsOfServiceDTO::class.java)
        emit(terms?.let { Response.Success(it) } ?: Response.Error(resourceProvider.getString(R.string.error_no_data)))
    }.catch { emit(Response.Error(it.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))) }

    override fun getTermsOfServiceUpdates(): Flow<Response<TermsOfServiceDTO>> = attachFirestoreDocumentListener(
        collection = FirestoreCollections.APP_CONFIG,
        documentId = FirestoreCollections.TERMS_OF_SERVICE
    ).map { snapshot ->
        val terms = snapshot.toObject(TermsOfServiceDTO::class.java)
        if (terms != null) {
            Response.Success(terms)
        } else {
            Response.Error(resourceProvider.getString(R.string.error_no_data))
        }
    }.catch { emit(Response.Error(it.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))) }

    private suspend fun getFirestoreDocumentData(collection: String, documentId: String): DocumentSnapshot? {
        return try {
            firestore.collection(collection).document(documentId)
                .get(Source.CACHE)
                .await()
                .takeIf { it.exists() }
                ?: firestore.collection(collection).document(documentId).get(Source.SERVER).await()
        } catch (e: Exception) {
            firestore.collection(collection).document(documentId).get(Source.SERVER).await()
        }
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
    ): Response<Unit> {
        return try {
            firestore.collection(collection)
                .document(id)
                .set(data, SetOptions.merge())
                .await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))
        }
    }

    private suspend fun deleteFirestoreDocument(
        id: String,
        collection: String
    ): Response<Unit> {
        return try {
            firestore.collection(collection)
                .document(id)
                .delete()
                .await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: resourceProvider.getString(R.string.error_unknown))
        }
    }
}