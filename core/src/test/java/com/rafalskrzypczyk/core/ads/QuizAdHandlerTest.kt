package com.rafalskrzypczyk.core.ads

import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QuizAdHandlerTest {

    private val isAdsFreeFlow = MutableStateFlow(false)
    private val premiumStatusProvider = mockk<PremiumStatusProvider> {
        every { isAdsFree } returns isAdsFreeFlow
    }
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var sut: QuizAdHandler

    @Before
    fun setUp() {
        isAdsFreeFlow.value = false
        sut = QuizAdHandler(premiumStatusProvider)
        sut.initialize(testScope)
    }

    @Test
    fun `should not show mid-quiz ad when count is below frequency`() {
        assertFalse(sut.shouldShowAd(answeredCount = 5, isQuizFinished = false))
        assertFalse(sut.shouldShowAd(answeredCount = 19, isQuizFinished = false))
    }

    @Test
    fun `should show mid-quiz ad when count reaches frequency`() {
        assertTrue(sut.shouldShowAd(answeredCount = 20, isQuizFinished = false))
    }

    @Test
    fun `should show exit ad when threshold is reached`() {
        assertTrue(sut.shouldShowAd(answeredCount = 10, isQuizFinished = true))
    }

    @Test
    fun `should not show exit ad when threshold is not reached`() {
        assertFalse(sut.shouldShowAd(answeredCount = 5, isQuizFinished = true))
        assertFalse(sut.shouldShowAd(answeredCount = 9, isQuizFinished = true))
    }

    @Test
    fun `should always show ad on finish when ignoreThreshold is true`() {
        assertTrue(sut.shouldShowAd(answeredCount = 3, isQuizFinished = true, ignoreThreshold = true))
    }

    @Test
    fun `should skip exit ad if mid-quiz ad was shown recently`() {
        // 1. Trigger mid-quiz ad at 20 (updates lastAdAnsweredCount to 20)
        assertTrue(sut.shouldShowAd(answeredCount = 20, isQuizFinished = false))
        
        // 2. Finish at 25. Delta is 5 (25-20). Threshold is 10.
        assertFalse(sut.shouldShowAd(answeredCount = 25, isQuizFinished = true))
    }

    @Test
    fun `should show exit ad if enough questions passed since mid-quiz ad`() {
        // 1. Trigger mid-quiz ad at 20 (updates lastAdAnsweredCount to 20)
        assertTrue(sut.shouldShowAd(answeredCount = 20, isQuizFinished = false))
        
        // 2. Finish at 31. Delta is 11 (31-20). Threshold is 10.
        assertTrue(sut.shouldShowAd(answeredCount = 31, isQuizFinished = true))
    }

    @Test
    fun `should never show ads if user has Premium`() {
        isAdsFreeFlow.value = true
        
        assertFalse(sut.shouldShowAd(answeredCount = 20, isQuizFinished = false))
        assertFalse(sut.shouldShowAd(answeredCount = 10, isQuizFinished = true))
        assertFalse(sut.shouldShowAd(answeredCount = 3, isQuizFinished = true, ignoreThreshold = true))
    }

    @Test
    fun `should handle ad dismissal based on finish state`() {
        // Scenario 1: Mid-quiz ad dismissal
        sut.shouldShowAd(answeredCount = 20, isQuizFinished = false)
        
        var midQuizContinued = false
        var midQuizFinished = false
        sut.handleAdDismissed(
            onContinue = { midQuizContinued = true },
            onFinish = { midQuizFinished = true }
        )
        assertTrue("Should continue quiz after mid-quiz ad", midQuizContinued)
        assertFalse("Should NOT finish quiz after mid-quiz ad", midQuizFinished)

        // Scenario 2: Exit ad dismissal
        // Threshold is 10. Answers 31 (so delta is 31-20 = 11 >= 10)
        sut.shouldShowAd(answeredCount = 31, isQuizFinished = true)
        
        var exitContinued = false
        var exitFinished = false
        sut.handleAdDismissed(
            onContinue = { exitContinued = true },
            onFinish = { exitFinished = true }
        )
        assertFalse("Should NOT continue quiz after exit ad", exitContinued)
        assertTrue("Should finish quiz after exit ad", exitFinished)
    }
}
