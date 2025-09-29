package com.rafalskrzypczyk.score

import app.cash.turbine.test
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.score.domain.Score
import com.rafalskrzypczyk.score.domain.ScoreManager
import com.rafalskrzypczyk.score.domain.ScoreRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScoreManagerTest {
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private lateinit var repository: ScoreRepository
    private lateinit var scoreManager: ScoreManager


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        scoreManager = ScoreManager(repository, testScope)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchUserScore emits success and sets score`() = testScope.runTest {
        val expectedScore = Score(10, 0,emptyList())
        coEvery { repository.getUserScore() } returns flowOf(Response.Success(expectedScore))

        scoreManager.onUserLogIn()
        advanceUntilIdle()

        assertEquals(expectedScore, scoreManager.getScore())
    }

    @Test
    fun `fetchUserScore emits error`() = testScope.runTest {
        val error = "Network Error"
        coEvery { repository.getUserScore() } returns flowOf(Response.Error(error))

        scoreManager.errorFlow.test {
            scoreManager.onUserLogIn()
            assertEquals(error, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateScore triggers debounce sync`() = testScope.runTest {
        coEvery { repository.saveUserScore(any()) } returns flowOf(Response.Success(Unit))

        val updatedScore = Score(5, 0,emptyList())
        scoreManager.updateScore(updatedScore)

        advanceTimeBy(30000)
        advanceUntilIdle()

        assertEquals(updatedScore, scoreManager.getScore())
    }

    @Test
    fun `forceSync does not sync if not dirty`() = testScope.runTest {
        scoreManager.forceSync()
        advanceUntilIdle()

        coVerify(exactly = 0) { repository.saveUserScore(any()) }
    }

    @Test
    fun `forceSync syncs when dirty`() = testScope.runTest {
        val score = Score(99, 0,emptyList())
        coEvery { repository.saveUserScore(score) } returns flowOf(Response.Success(Unit))

        scoreManager.updateScore(score)
        scoreManager.forceSync()
        advanceUntilIdle()

        assertEquals(score, scoreManager.getScore())
    }

    @Test
    fun `syncScore emits error`() = testScope.runTest {
        val error = "Write failed"
        coEvery { repository.saveUserScore(any()) } returns flowOf(Response.Error(error))

        scoreManager.updateScore(Score(1, 0,emptyList()))
        advanceTimeBy(30000)

        scoreManager.errorFlow.test {
            assertEquals(error, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}