package com.rafalskrzypczyk.firestore.domain.use_cases

import app.cash.turbine.test
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.firestore.domain.models.TermsOfServiceDTO
import com.rafalskrzypczyk.firestore.domain.models.TermsOfServiceStatus
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetTermsOfServiceUCTest {

    private val firestoreApi: FirestoreApi = mockk()
    private val useCase = GetTermsOfServiceUC(firestoreApi)

    @Test
    fun `when fetch terms then return response from firestore`() = runTest {
        val terms = TermsOfServiceDTO(version = 1, content = "Terms")
        every { firestoreApi.getTermsOfService() } returns flowOf(Response.Loading, Response.Success(terms))

        useCase.invoke().test {
            assertEquals(Response.Loading, awaitItem())
            assertEquals(Response.Success(terms), awaitItem())
            awaitComplete()
        }
    }
}
