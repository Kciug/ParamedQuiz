package com.rafalskrzypczyk.firestore.domain.use_cases

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.domain.UserFeedback
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.firestore.domain.models.FeedbackDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveFeedbackUC @Inject constructor(
    private val firestoreApi: FirestoreApi
) {
    operator fun invoke(feedback: UserFeedback): Flow<Response<Unit>> {
        return firestoreApi.saveFeedback(
            FeedbackDTO(
                id = feedback.id,
                userId = feedback.userId,
                feedback = feedback.feedback,
                rating = feedback.rating,
                timestamp = feedback.timestamp
            )
        )
    }
}
