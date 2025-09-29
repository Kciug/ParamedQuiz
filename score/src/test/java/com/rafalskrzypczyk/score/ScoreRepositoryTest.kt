package com.rafalskrzypczyk.score

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.user_management.UserData
import com.rafalskrzypczyk.core.user_management.UserManager
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.firestore.domain.models.ScoreDTO
import com.rafalskrzypczyk.score.di.ScoreRepositoryImpl
import com.rafalskrzypczyk.score.domain.Score
import com.rafalskrzypczyk.score.domain.ScoreStorage
import com.rafalskrzypczyk.score.domain.toDTO
import com.rafalskrzypczyk.score.domain.toDomain
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScoreRepositoryTest {
    private lateinit var firestore: FirestoreApi
    private lateinit var userManager: UserManager
    private lateinit var scoreStorage: ScoreStorage
    private lateinit var repository: ScoreRepositoryImpl

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        firestore = mockk(relaxed = true)
        userManager = mockk(relaxed = true)
        scoreStorage = mockk(relaxed = true)
        repository = ScoreRepositoryImpl(firestore, userManager, scoreStorage)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getUserScore returns score from firestore when user is logged in`() = testScope.runTest {
        val user = UserData("id123", "test@test.com", "test")
        val dto = ScoreDTO(10, 0, null,emptyList())
        val domain = dto.toDomain()

        every { userManager.getCurrentLoggedUser() } returns user
        coEvery { firestore.getUserScore(user.id) } returns flowOf(Response.Success(dto))

        val result = repository.getUserScore().first()

        assertEquals(Response.Success(domain), result)
    }

    @Test
    fun `getUserScore returns local score when user is not logged in`() = testScope.runTest {
        val localScore = Score(5, 0, null,listOf())

        every { userManager.getCurrentLoggedUser() } returns null
        every { scoreStorage.getScore() } returns localScore

        val result = repository.getUserScore().first()

        assertEquals(Response.Success(localScore), result)
    }

    @Test
    fun `saveUserScore stores remotely when user is logged in`() = runTest {
        val user = UserData("id123", "test@test.com", "test")
        val score = Score(100, 0, null,listOf())

        every { userManager.getCurrentLoggedUser() } returns user
        coEvery { firestore.updateUserScore(eq(user.id), any()) } returns flowOf(Response.Success(Unit))

        val result = repository.saveUserScore(score).first()

        assertEquals(Response.Success(Unit), result)
        coVerify { firestore.updateUserScore(user.id, score.toDTO()) }
    }

    @Test
    fun `saveUserScore stores locally when user is not logged in`() = runTest {
        val score = Score(100, 0, null,listOf())

        every { userManager.getCurrentLoggedUser() } returns null
        every { scoreStorage.saveScore(score) } just Runs

        val result = repository.saveUserScore(score).first()

        assertEquals(Response.Success(Unit), result)
        verify { scoreStorage.saveScore(score) }
    }

    @Test
    fun `deleteUserScore deletes remotely when user is logged in`() = runTest {
        val user = UserData("id123", "test@test.com", "test")

        every { userManager.getCurrentLoggedUser() } returns user
        coEvery { firestore.deleteUserScore(user.id) } returns flowOf(Response.Success(Unit))

        val result = repository.deleteUserScore().first()

        assertEquals(Response.Success(Unit), result)
        coVerify { firestore.deleteUserScore(user.id) }
    }

    @Test
    fun `deleteUserScore returns error when user not logged in`() = runTest {
        every { userManager.getCurrentLoggedUser() } returns null

        val result = repository.deleteUserScore().first()

        assertTrue(result is Response.Error)
        assertEquals("User not found", (result as Response.Error).error)
    }

    @Test
    fun `clearLocalScoreData clears score storage`() {
        repository.clearLocalScoreData()

        verify { scoreStorage.clearScore() }
    }
}