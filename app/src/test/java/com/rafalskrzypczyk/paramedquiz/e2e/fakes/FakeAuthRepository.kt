package com.rafalskrzypczyk.paramedquiz.e2e.fakes

import android.content.Context
import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.user_management.UserAuthenticationMethod
import com.rafalskrzypczyk.core.user_management.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Fake uwierzytelniania bez Firebase. Domyślnie użytkownik **niezalogowany** (gość).
 * Logowanie/rejestracja zwracają sukces i ustawiają stan zalogowania (na potrzeby scenariuszy konta).
 */
class FakeAuthRepository : AuthRepository {

    var loggedIn: Boolean = false
    var currentUser: UserData = UserData(
        id = "test-user",
        email = "test@example.com",
        name = "Tester",
        authenticationMethod = UserAuthenticationMethod.PASSWORD
    )

    override fun isUserLoggedIn(): Boolean = loggedIn

    override fun loginWithEmailAndPassword(email: String, password: String): Flow<Response<UserData>> {
        loggedIn = true
        return flowOf(Response.Success(currentUser.copy(email = email)))
    }

    override fun registerWithEmailAndPassword(
        email: String,
        password: String,
        userName: String
    ): Flow<Response<UserData>> {
        loggedIn = true
        return flowOf(Response.Success(currentUser.copy(email = email, name = userName)))
    }

    override fun signOut() {
        loggedIn = false
    }

    override fun sendPasswordResetToEmail(email: String): Flow<Response<Unit>> =
        flowOf(Response.Success(Unit))

    override fun reauthenticateWithPassword(email: String, password: String): Flow<Response<Unit>> =
        flowOf(Response.Success(Unit))

    override fun reauthenticateWithProvider(context: Context): Flow<Response<Unit>> =
        flowOf(Response.Success(Unit))

    override fun changePassword(newPassword: String): Flow<Response<Unit>> =
        flowOf(Response.Success(Unit))

    override fun changeUserName(newUsername: String): Flow<Response<UserData>> {
        currentUser = currentUser.copy(name = newUsername)
        return flowOf(Response.Success(currentUser))
    }

    override fun deleteUser(): Flow<Response<Unit>> {
        loggedIn = false
        return flowOf(Response.Success(Unit))
    }

    override fun signInWithGoogle(context: Context): Flow<Response<UserData>> {
        loggedIn = true
        return flowOf(Response.Success(currentUser))
    }
}
