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
    private val sharedPrefs: SharedPreferencesApi = mockk(relaxed = true)
    private val useCase = GetTermsOfServiceUC(firestoreApi, sharedPrefs)

    @Test
    fun `when terms already accepted and remote version same then emit Accepted`() = runTest {
        val remoteTerms = TermsOfServiceDTO(version = 1, content = "Same terms")
        every { sharedPrefs.getAcceptedTermsVersion() } returns 1
        every { firestoreApi.getTermsOfServiceUpdates() } returns flowOf(Response.Success(remoteTerms))

        useCase.invoke().test {
            assertEquals(TermsOfServiceStatus.Accepted, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `when new version available then emit NeedsAcceptance`() = runTest {
        val remoteTerms = TermsOfServiceDTO(version = 2, content = "New terms")
        every { sharedPrefs.getAcceptedTermsVersion() } returns 1
        every { firestoreApi.getTermsOfServiceUpdates() } returns flowOf(Response.Loading, Response.Success(remoteTerms))

        useCase.invoke().test {
            assertEquals(TermsOfServiceStatus.Loading, awaitItem())
            assertEquals(TermsOfServiceStatus.NeedsAcceptance(remoteTerms), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `when error fetching and no previous version then emit Error`() = runTest {
        every { sharedPrefs.getAcceptedTermsVersion() } returns -1
        every { firestoreApi.getTermsOfServiceUpdates() } returns flowOf(Response.Loading, Response.Error("Network error"))

        useCase.invoke().test {
            assertEquals(TermsOfServiceStatus.Loading, awaitItem())
            assertEquals(TermsOfServiceStatus.Error("Network error"), awaitItem())
            awaitComplete()
        }
    }
}
