package com.rafalskrzypczyk.home_screen.domain.use_cases

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.firestore.domain.models.NewsBannerDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNewsBannersUC @Inject constructor(
    private val firestoreApi: FirestoreApi
) {
    operator fun invoke(): Flow<Response<List<NewsBannerDTO>>> = firestoreApi.getNewsBanners()
}
