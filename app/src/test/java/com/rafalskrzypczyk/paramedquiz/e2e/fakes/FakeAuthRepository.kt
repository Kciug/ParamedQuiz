package com.rafalskrzypczyk.paramedquiz.e2e.fakes

import android.content.Context
import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.error.AppError
import com.rafalskrzypczyk.core.user_management.UserAuthenticationMethod
import com.rafalskrzypczyk.core.user_management.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Fake uwierzytelniania bez Firebase. Domyślnie użytkownik **niezalogowany** (gość).
 * Logowanie/rejestracja zwracają sukces i ustawiają stan zalogowania (na potrzeby scenariuszy konta).
 *
 * Ustawienie [nextError] przełącza wszystkie operacje w tryb awarii, co pozwala pisać
 * scenariusze błędów bez dotykania Firebase.
 */
class FakeAuthRepository : AuthRepository {

    var loggedIn: Boolean = false
    var currentUser: UserData = UserData(
        id = "test-user",
        email = "test@example.com",
        name = "Tester",
        authenticationMethod = UserAuthenticationMethod.PASSWORD
    )
    var nextError: AppError? = null

    override fun isUserLoggedIn(): Boolean = loggedIn

    override fun loginWithEmailAndPassword(email: String, password: String): Flow<Response<UserData>> {
        nextError?.let { return flowOf(Response.Error(it)) }
        loggedIn = true
        return flowOf(Response.Success(currentUser.copy(email = email)))
    }

    override fun registerWithEmailAndPassword(
        email: String,
        password: String,
        userName: String
    ): Flow<Response<UserData>> {
        nextError?.let { return flowOf(Response.Error(it)) }
        loggedIn = true
        return flowOf(Response.Success(currentUser.copy(email = email, name = userName)))
    }

    override suspend fun signOut() {
        loggedIn = false
    }

    override fun sendPasswordResetToEmail(email: String): Flow<Response<Unit>> =
        nextError?.let { flowOf(Response.Error(it)) } ?: flowOf(Response.Success(Unit))

    override fun reauthenticateWithPassword(email: String, password: String): Flow<Response<Unit>> =
        nextError?.let { flowOf(Response.Error(it)) } ?: flowOf(Response.Success(Unit))

    override fun reauthenticateWithProvider(context: Context): Flow<Response<Unit>> =
        nextError?.let { flowOf(Response.Error(it)) } ?: flowOf(Response.Success(Unit))

    override fun changePassword(newPassword: String): Flow<Response<Unit>> =
        nextError?.let { flowOf(Response.Error(it)) } ?: flowOf(Response.Success(Unit))

    override fun changeUserName(newUsername: String): Flow<Response<UserData>> {
        nextError?.let { return flowOf(Response.Error(it)) }
        currentUser = currentUser.copy(name = newUsername)
        return flowOf(Response.Success(currentUser))
    }

    override fun deleteUser(): Flow<Response<Unit>> {
        nextError?.let { return flowOf(Response.Error(it)) }
        loggedIn = false
        return flowOf(Response.Success(Unit))
    }

    override fun signInWithGoogle(context: Context): Flow<Response<UserData>> {
        nextError?.let { return flowOf(Response.Error(it)) }
        loggedIn = true
        return flowOf(Response.Success(currentUser))
    }
}
