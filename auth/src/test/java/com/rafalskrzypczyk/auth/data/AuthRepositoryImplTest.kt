package com.rafalskrzypczyk.auth.data

import android.content.Context
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import app.cash.turbine.test
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AdditionalUserInfo
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserInfo
import com.rafalskrzypczyk.auth.domain.GoogleCredentialsProvider
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.error.AppError
import com.rafalskrzypczyk.core.error.ErrorLogger
import com.rafalskrzypczyk.core.user_management.UserManager
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.firestore.domain.models.UserDataDTO
import com.rafalskrzypczyk.score.domain.ScoreManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private const val TEST_UID = "uid-1"
private const val TEST_EMAIL = "tester@example.com"
private const val TEST_NAME = "Tester"
private const val TEST_ID_TOKEN = "google-id-token"

class AuthRepositoryImplTest {

    private val firebaseAuth: FirebaseAuth = mockk(relaxed = true)
    private val firestoreApi: FirestoreApi = mockk(relaxed = true)
    private val userManager: UserManager = mockk(relaxed = true)
    private val errorLogger: ErrorLogger = mockk(relaxed = true)
    private val googleCredentialsProvider: GoogleCredentialsProvider = mockk(relaxed = true)
    private val scoreManager: ScoreManager = mockk(relaxed = true)
    private val context: Context = mockk(relaxed = true)

    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setUp() {
        mockkStatic(GoogleAuthProvider::class)
        every { GoogleAuthProvider.getCredential(any(), any()) } returns mockk<AuthCredential>(relaxed = true)

        repository = AuthRepositoryImpl(
            firebaseAuth = firebaseAuth,
            firestoreApi = firestoreApi,
            userManager = userManager,
            authErrorMapper = AuthErrorMapper(errorLogger),
            googleCredentialsProvider = googleCredentialsProvider,
            scoreManager = scoreManager
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `signInWithGoogle emits nothing and completes when user dismisses the account picker`() = runTest {
        coEvery { googleCredentialsProvider.getGoogleIdToken(context) } throws GetCredentialCancellationException()

        repository.signInWithGoogle(context).test {
            awaitComplete()
        }

        verify(exactly = 0) { errorLogger.log(any(), any(), any()) }
    }

    @Test
    fun `signInWithGoogle reports missing google account and completes`() = runTest {
        coEvery { googleCredentialsProvider.getGoogleIdToken(context) } throws NoCredentialException()

        repository.signInWithGoogle(context).test {
            assertEquals(Response.Error(AppError.Google.NoCredentialAvailable), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `signInWithGoogle distinguishes a non google credential from a failed sign in`() = runTest {
        coEvery { googleCredentialsProvider.getGoogleIdToken(context) } returns null

        repository.signInWithGoogle(context).test {
            assertEquals(Response.Loading, awaitItem())
            assertEquals(Response.Error(AppError.Google.UnsupportedCredential), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `signInWithGoogle reports incomplete sign in when firebase returns no user`() = runTest {
        coEvery { googleCredentialsProvider.getGoogleIdToken(context) } returns TEST_ID_TOKEN
        every { firebaseAuth.signInWithCredential(any()) } returns completedTask(authResult(user = null))

        repository.signInWithGoogle(context).test {
            assertEquals(Response.Loading, awaitItem())
            assertEquals(Response.Error(AppError.Auth.SignInIncomplete), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `signInWithGoogle maps a firebase auth failure to its concrete error`() = runTest {
        coEvery { googleCredentialsProvider.getGoogleIdToken(context) } returns TEST_ID_TOKEN
        every { firebaseAuth.signInWithCredential(any()) } returns
                failedTask(authException("ERROR_INVALID_CREDENTIAL"))

        repository.signInWithGoogle(context).test {
            assertEquals(Response.Loading, awaitItem())
            assertEquals(Response.Error(AppError.Auth.InvalidCredentials), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `signInWithGoogle registers a new user and completes`() = runTest {
        coEvery { googleCredentialsProvider.getGoogleIdToken(context) } returns TEST_ID_TOKEN
        every { firebaseAuth.signInWithCredential(any()) } returns
                completedTask(authResult(user = firebaseUser(), isNewUser = true))
        every { firestoreApi.updateUserData(any()) } returns flowOf(Response.Loading, Response.Success(Unit))

        repository.signInWithGoogle(context).test {
            assertEquals(Response.Loading, awaitItem())
            assertEquals(Response.Loading, awaitItem())
            val success = awaitItem()
            assertTrue(success is Response.Success)
            assertEquals(TEST_UID, (success as Response.Success).data.id)
            awaitComplete()
        }

        verify(exactly = 1) { scoreManager.onUserRegister() }
    }

    @Test
    fun `signInWithGoogle surfaces a typed error instead of crashing on incomplete provider data`() = runTest {
        coEvery { googleCredentialsProvider.getGoogleIdToken(context) } returns TEST_ID_TOKEN
        every { firebaseAuth.signInWithCredential(any()) } returns
                completedTask(authResult(user = firebaseUser(providerData = listOf(userInfo("firebase"))), isNewUser = true))

        repository.signInWithGoogle(context).test {
            assertEquals(Response.Loading, awaitItem())
            val error = awaitItem()
            assertTrue(error is Response.Error)
            assertEquals(AppError.Auth.Unknown("AUTH:IndexOutOfBoundsException"), (error as Response.Error).error)
            awaitComplete()
        }
    }

    @Test
    fun `loginWithEmailAndPassword passes a data layer error through without logging it again`() = runTest {
        every { firebaseAuth.signInWithEmailAndPassword(any(), any()) } returns
                completedTask(authResult(user = firebaseUser()))
        every { firestoreApi.getUserData(TEST_UID) } returns flowOf(Response.Error(AppError.Data.PermissionDenied))

        repository.loginWithEmailAndPassword(TEST_EMAIL, "secret").test {
            assertEquals(Response.Loading, awaitItem())
            assertEquals(Response.Error(AppError.Data.PermissionDenied), awaitItem())
            awaitComplete()
        }

        verify(exactly = 0) { errorLogger.log(any(), any(), any()) }
    }

    @Test
    fun `loginWithEmailAndPassword loads user data and completes`() = runTest {
        every { firebaseAuth.signInWithEmailAndPassword(any(), any()) } returns
                completedTask(authResult(user = firebaseUser()))
        every { firestoreApi.getUserData(TEST_UID) } returns
                flowOf(Response.Success(UserDataDTO(id = TEST_UID, name = TEST_NAME)))

        repository.loginWithEmailAndPassword(TEST_EMAIL, "secret").test {
            assertEquals(Response.Loading, awaitItem())
            val success = awaitItem()
            assertTrue(success is Response.Success)
            assertEquals(TEST_NAME, (success as Response.Success).data.name)
            awaitComplete()
        }

        verify(exactly = 1) { scoreManager.onUserLogIn() }
    }

    @Test
    fun `changePassword reports a missing session instead of hanging`() = runTest {
        every { firebaseAuth.currentUser } returns null

        repository.changePassword("newSecret").test {
            assertEquals(Response.Loading, awaitItem())
            assertEquals(Response.Error(AppError.Auth.UserNotLoggedIn), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `sendPasswordResetToEmail maps failures and completes`() = runTest {
        every { firebaseAuth.sendPasswordResetEmail(any()) } returns
                failedTask(authException("ERROR_INVALID_EMAIL"))

        repository.sendPasswordResetToEmail(TEST_EMAIL).test {
            assertEquals(Response.Loading, awaitItem())
            assertEquals(Response.Error(AppError.Auth.InvalidEmail), awaitItem())
            awaitComplete()
        }
    }

    private fun authException(code: String): FirebaseAuthException =
        mockk<FirebaseAuthException>(relaxed = true).also { every { it.errorCode } returns code }

    private fun userInfo(providerId: String): UserInfo =
        mockk<UserInfo>(relaxed = true).also { every { it.providerId } returns providerId }

    private fun firebaseUser(
        providerData: List<UserInfo> = listOf(userInfo("firebase"), userInfo("google.com"))
    ): FirebaseUser = mockk<FirebaseUser>(relaxed = true).also {
        every { it.uid } returns TEST_UID
        every { it.email } returns TEST_EMAIL
        every { it.displayName } returns TEST_NAME
        every { it.providerData } returns providerData.toMutableList()
    }

    private fun authResult(user: FirebaseUser?, isNewUser: Boolean = false): AuthResult =
        mockk<AuthResult>(relaxed = true).also {
            every { it.user } returns user
            every { it.additionalUserInfo } returns
                    mockk<AdditionalUserInfo>(relaxed = true).also { info -> every { info.isNewUser } returns isNewUser }
        }

    private fun <T> completedTask(result: T): Task<T> = mockk<Task<T>>(relaxed = true).also {
        every { it.isComplete } returns true
        every { it.isCanceled } returns false
        every { it.exception } returns null
        every { it.result } returns result
    }

    private fun <T> failedTask(error: Exception): Task<T> = mockk<Task<T>>(relaxed = true).also {
        every { it.isComplete } returns true
        every { it.isCanceled } returns false
        every { it.exception } returns error
    }
}
