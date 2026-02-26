package com.rafalskrzypczyk.firestore.domain.use_cases

import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQuestionsCountUC @Inject constructor(
    private val firestoreApi: FirestoreApi
) {
    operator fun invoke(collection: String): Flow<Int> = firestoreApi.getQuestionsCountUpdates(collection)
}
