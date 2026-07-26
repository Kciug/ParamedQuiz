@file:OptIn(ExperimentalCoroutinesApi::class)

package com.rafalskrzypczyk.signup.login

import android.content.Context
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

class LoginVMTest {

    private val authRepository: AuthRepository = mockk(relaxed = true)
    private val context: Context = mockk(relaxed = true)

    private lateinit var viewModel: LoginVM

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = LoginVM(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `surfaces the typed error and stops loading when sign in fails`() = runTest {
        every { authRepository.signInWithGoogle(context) } returns
                flowOf(Response.Loading, Response.Error(AppError.Google.NoCredentialAvailable))

        viewModel.onEvent(LoginUIEvents.LoginWithGoogle(context))

        assertEquals(AppError.Google.NoCredentialAvailable, viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `marks loading while the repository works`() = runTest {
        every { authRepository.signInWithGoogle(context) } returns flowOf(Response.Loading)

        viewModel.onEvent(LoginUIEvents.LoginWithGoogle(context))

        assertTrue(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `marks success when the repository returns user data`() = runTest {
        every { authRepository.loginWithEmailAndPassword(any(), any()) } returns
                flowOf(Response.Loading, Response.Success(userData()))

        viewModel.onEvent(LoginUIEvents.LoginWithCredentials("tester@example.com", "secret"))

        assertTrue(viewModel.state.value.isSuccess)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `clears the error on user acknowledgement`() = runTest {
        every { authRepository.loginWithEmailAndPassword(any(), any()) } returns
                flowOf(Response.Error(AppError.Auth.WrongPassword))
        viewModel.onEvent(LoginUIEvents.LoginWithCredentials("tester@example.com", "wrong"))

        viewModel.onEvent(LoginUIEvents.ClearError)

        assertNull(viewModel.state.value.error)
    }

    private fun userData() = UserData(
        id = "uid-1",
        email = "tester@example.com",
        name = "Tester",
        authenticationMethod = UserAuthenticationMethod.NONPASSWORD
    )
}
