package com.rafalskrzypczyk.firestore.data

import com.google.firebase.firestore.FirebaseFirestoreException
import com.rafalskrzypczyk.core.error.AppError
import com.rafalskrzypczyk.core.error.ErrorLogger
import com.rafalskrzypczyk.core.network.NetworkMonitor
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException
import java.net.UnknownHostException

private const val ORIGIN = "Test.origin"

class FirestoreErrorMapperTest {

    private val errorLogger: ErrorLogger = mockk(relaxed = true)
    private val networkMonitor: NetworkMonitor = mockk<NetworkMonitor>().also { every { it.isOnline() } returns true }
    private val mapper = FirestoreErrorMapper(errorLogger, networkMonitor)

    private fun firestoreException(code: FirebaseFirestoreException.Code) =
        FirebaseFirestoreException("boom", code)

    @Test
    fun `maps known firestore codes`() {
        val expected = mapOf(
            FirebaseFirestoreException.Code.PERMISSION_DENIED to AppError.Data.PermissionDenied,
            FirebaseFirestoreException.Code.UNAVAILABLE to AppError.Data.Unavailable,
            FirebaseFirestoreException.Code.ABORTED to AppError.Data.Aborted,
            FirebaseFirestoreException.Code.NOT_FOUND to AppError.Data.NotFound,
            FirebaseFirestoreException.Code.DEADLINE_EXCEEDED to AppError.Data.DeadlineExceeded
        )

        expected.forEach { (code, error) ->
            assertEquals(error, mapper.toAppError(ORIGIN, firestoreException(code)))
        }
    }

    @Test
    fun `maps unauthenticated to auth error`() {
        assertEquals(
            AppError.Auth.UserNotLoggedIn,
            mapper.toAppError(ORIGIN, firestoreException(FirebaseFirestoreException.Code.UNAUTHENTICATED))
        )
    }

    @Test
    fun `maps unhandled firestore code to prefixed unknown`() {
        assertEquals(
            AppError.Data.Unknown("FS:UNIMPLEMENTED"),
            mapper.toAppError(ORIGIN, firestoreException(FirebaseFirestoreException.Code.UNIMPLEMENTED))
        )
    }

    @Test
    fun `maps io failures to no network`() {
        assertEquals(AppError.NoNetwork, mapper.toAppError(ORIGIN, IOException("socket closed")))
        assertEquals(AppError.NoNetwork, mapper.toAppError(ORIGIN, UnknownHostException("firestore.googleapis.com")))
    }

    @Test
    fun `never leaks a raw exception message as user facing error`() {
        val raw = RuntimeException("Unable to resolve host firestore.googleapis.com")

        val error = mapper.toAppError(ORIGIN, raw)

        assertEquals(AppError.Data.Unknown("FS:RuntimeException"), error)
    }

    @Test
    fun `reports lack of connectivity instead of a service outage when the device is offline`() {
        every { networkMonitor.isOnline() } returns false

        assertEquals(
            AppError.NoNetwork,
            mapper.toAppError(ORIGIN, firestoreException(FirebaseFirestoreException.Code.UNAVAILABLE))
        )
        assertEquals(
            AppError.NoNetwork,
            mapper.toAppError(ORIGIN, firestoreException(FirebaseFirestoreException.Code.DEADLINE_EXCEEDED))
        )
    }

    @Test
    fun `keeps the service outage message when the device is online`() {
        every { networkMonitor.isOnline() } returns true

        assertEquals(
            AppError.Data.Unavailable,
            mapper.toAppError(ORIGIN, firestoreException(FirebaseFirestoreException.Code.UNAVAILABLE))
        )
        assertEquals(
            AppError.Data.DeadlineExceeded,
            mapper.toAppError(ORIGIN, firestoreException(FirebaseFirestoreException.Code.DEADLINE_EXCEEDED))
        )
    }

    @Test
    fun `does not mask a permission failure as a connectivity problem`() {
        every { networkMonitor.isOnline() } returns false

        assertEquals(
            AppError.Data.PermissionDenied,
            mapper.toAppError(ORIGIN, firestoreException(FirebaseFirestoreException.Code.PERMISSION_DENIED))
        )
    }

    @Test
    fun `logs every failure once with its cause`() {
        val exception = firestoreException(FirebaseFirestoreException.Code.PERMISSION_DENIED)

        mapper.toAppError(ORIGIN, exception)

        verify(exactly = 1) { errorLogger.log(ORIGIN, AppError.Data.PermissionDenied, exception) }
    }
}
