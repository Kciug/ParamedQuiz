package com.rafalskrzypczyk.home_screen.domain.use_cases

import app.cash.turbine.test
import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.core.api_response.Response
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SignOutUCTest {

    private val authRepository: AuthRepository = mockk(relaxed = true)
    private val useCase = SignOutUC(authRepository)

    @Test
    fun `emits loading and then success`() = runTest {
        useCase().test {
            assertEquals(Response.Loading, awaitItem())
            assertEquals(Response.Success(Unit), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `reports success only after the repository finished signing out`() = runTest {
        var signedOut = false
        coEvery { authRepository.signOut() } coAnswers { signedOut = true }

        useCase().test {
            skipItems(1)
            assertEquals(Response.Success(Unit), awaitItem())
            assertEquals(true, signedOut)
            awaitComplete()
        }

        coVerify(exactly = 1) { authRepository.signOut() }
    }
}
