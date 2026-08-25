package com.rafalskrzypczyk.auth.data

import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCustomException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.GetCredentialUnsupportedException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.rafalskrzypczyk.core.error.AppError
import com.rafalskrzypczyk.core.error.ErrorLogger
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

private const val ORIGIN = "Test.origin"

class AuthErrorMapperTest {

    private val errorLogger: ErrorLogger = mockk(relaxed = true)
    private val mapper = AuthErrorMapper(errorLogger)

    private fun authException(code: String): FirebaseAuthException =
        mockk<FirebaseAuthException>(relaxed = true) { every { errorCode } returns code }

    @Test
    fun `maps known firebase auth codes`() {
        val expected = mapOf(
            "ERROR_INVALID_CREDENTIAL" to AppError.Auth.InvalidCredentials,
            "ERROR_USER_NOT_FOUND" to AppError.Auth.InvalidCredentials,
            "ERROR_INVALID_EMAIL" to AppError.Auth.InvalidEmail,
            "ERROR_WRONG_PASSWORD" to AppError.Auth.WrongPassword,
            "ERROR_EMAIL_ALREADY_IN_USE" to AppError.Auth.EmailAlreadyInUse,
            "ERROR_WEAK_PASSWORD" to AppError.Auth.WeakPassword,
            "ERROR_OPERATION_NOT_ALLOWED" to AppError.Auth.OperationNotAllowed,
            "ERROR_TOO_MANY_REQUESTS" to AppError.Auth.TooManyRequests,
            "ERROR_REQUIRES_RECENT_LOGIN" to AppError.Auth.RecentLoginRequired
        )

        expected.forEach { (code, error) ->
            assertEquals(error, mapper.toAppError(ORIGIN, authException(code)))
        }
    }

    @Test
    fun `maps unknown firebase auth code to prefixed unknown`() {
        assertEquals(
            AppError.Auth.Unknown("AUTH:ERROR_SOMETHING_NEW"),
            mapper.toAppError(ORIGIN, authException("ERROR_SOMETHING_NEW"))
        )
    }

    @Test
    fun `maps credential manager exceptions to distinct google errors`() {
        assertEquals(AppError.Google.Cancelled, mapper.toAppError(ORIGIN, GetCredentialCancellationException()))
        assertEquals(AppError.Google.NoCredentialAvailable, mapper.toAppError(ORIGIN, NoCredentialException()))
        assertEquals(AppError.Google.ProviderConfiguration, mapper.toAppError(ORIGIN, GetCredentialProviderConfigurationException()))
        assertEquals(AppError.Google.Interrupted, mapper.toAppError(ORIGIN, GetCredentialInterruptedException()))
        assertEquals(AppError.Google.UnsupportedCredential, mapper.toAppError(ORIGIN, GetCredentialUnsupportedException()))
    }

    @Test
    fun `maps token parsing failure to malformed id token`() {
        assertEquals(
            AppError.Google.MalformedIdToken,
            mapper.toAppError(ORIGIN, GoogleIdTokenParsingException(RuntimeException("bad token")))
        )
    }

    @Test
    fun `maps unrecognised credential exception to prefixed unknown with type`() {
        val exception = GetCredentialCustomException("com.example.CUSTOM_TYPE")

        assertEquals(AppError.Google.Unknown("GIS:com.example.CUSTOM_TYPE"), mapper.toAppError(ORIGIN, exception))
    }

    @Test
    fun `maps firebase network exception to no network`() {
        assertEquals(AppError.NoNetwork, mapper.toAppError(ORIGIN, FirebaseNetworkException("offline")))
    }

    @Test
    fun `maps foreign exception to prefixed unknown with class name`() {
        assertEquals(
            AppError.Auth.Unknown("AUTH:IndexOutOfBoundsException"),
            mapper.toAppError(ORIGIN, IndexOutOfBoundsException("Index 1 out of bounds for length 1"))
        )
    }

    @Test
    fun `does not log user cancellation`() {
        mapper.toAppError(ORIGIN, GetCredentialCancellationException())

        verify(exactly = 0) { errorLogger.log(any(), any(), any()) }
    }

    @Test
    fun `logs every genuine failure once with its cause`() {
        val exception = NoCredentialException()

        mapper.toAppError(ORIGIN, exception)

        verify(exactly = 1) { errorLogger.log(ORIGIN, AppError.Google.NoCredentialAvailable, exception) }
    }

    @Test
    fun `report logs error without cause and returns it`() {
        val result = mapper.report(ORIGIN, AppError.Auth.UserNotLoggedIn)

        assertEquals(AppError.Auth.UserNotLoggedIn, result)
        verify(exactly = 1) { errorLogger.log(ORIGIN, AppError.Auth.UserNotLoggedIn, null) }
    }
}
