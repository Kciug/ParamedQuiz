@file:OptIn(ExperimentalCoroutinesApi::class)

package com.rafalskrzypczyk.signup.register

import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.error.AppError
import com.rafalskrzypczyk.core.user_management.UserAuthenticationMethod
import com.rafalskrzypczyk.core.user_management.UserData
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RegisterVMTest {

    private val authRepository: AuthRepository = mockk(relaxed = true)

    private lateinit var viewModel: RegisterVM

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = RegisterVM(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `surfaces the typed error and stops loading when registration fails`() = runTest {
        every { authRepository.registerWithEmailAndPassword(any(), any(), any()) } returns
                flowOf(Response.Loading, Response.Error(AppError.Auth.EmailAlreadyInUse))

        viewModel.onEvent(RegisterUIEvents.RegisterWithCredentials("Tester", "tester@example.com", "secret"))

        assertEquals(AppError.Auth.EmailAlreadyInUse, viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `marks success when registration completes`() = runTest {
        every { authRepository.registerWithEmailAndPassword(any(), any(), any()) } returns
                flowOf(Response.Loading, Response.Success(userData()))

        viewModel.onEvent(RegisterUIEvents.RegisterWithCredentials("Tester", "tester@example.com", "secret"))

        assertTrue(viewModel.state.value.isSuccess)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `clears the error on user acknowledgement`() = runTest {
        every { authRepository.registerWithEmailAndPassword(any(), any(), any()) } returns
                flowOf(Response.Error(AppError.Auth.WeakPassword))
        viewModel.onEvent(RegisterUIEvents.RegisterWithCredentials("Tester", "tester@example.com", "123"))

        viewModel.onEvent(RegisterUIEvents.ClearError)

        assertNull(viewModel.state.value.error)
    }

    private fun userData() = UserData(
        id = "uid-1",
        email = "tester@example.com",
        name = "Tester",
        authenticationMethod = UserAuthenticationMethod.PASSWORD
    )
}
