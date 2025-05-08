package com.rafalskrzypczyk.firestore.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.rafalskrzypczyk.core.R
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.utils.ResourceProvider
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.firestore.domain.models.CategoryDTO
import com.rafalskrzypczyk.firestore.domain.models.QuestionDTO
import com.rafalskrzypczyk.firestore.domain.models.SwipeQuestionDTO
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