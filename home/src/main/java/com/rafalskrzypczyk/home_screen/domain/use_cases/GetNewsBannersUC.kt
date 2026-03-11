package com.rafalskrzypczyk.home_screen.domain.use_cases

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.firestore.domain.models.NewsBannerDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetNewsBannersUC @Inject constructor(
    private val firestoreApi: FirestoreApi,
    private val sharedPreferencesApi: SharedPreferencesApi
) {
    operator fun invoke(): Flow<Response<List<NewsBannerDTO>>> = firestoreApi.getNewsBannerUpdates().map { banners ->
        val seenIds = sharedPreferencesApi.getSeenNewsIds()
        Response.Success(banners.filter { it.id !in seenIds })
    }
}
