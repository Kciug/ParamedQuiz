package com.rafalskrzypczyk.score

import com.rafalskrzypczyk.score.domain.Score
import com.rafalskrzypczyk.score.domain.ScoreManager
import com.rafalskrzypczyk.score.domain.use_cases.UpdateBestSwipeComboUC
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class UpdateBestSwipeComboUCTest {
    private lateinit var scoreManager: ScoreManager
    private lateinit var updateBestSwipeComboUC: UpdateBestSwipeComboUC

    @Before
    fun setup() {
        scoreManager = mockk(relaxed = true)
        updateBestSwipeComboUC = UpdateBestSwipeComboUC(scoreManager)
    }

    @Test
    fun `should persist combo when it beats the stored best`() {
        // given
        every { scoreManager.getScore() } returns Score(0, 0, null, null, emptyList(), bestSwipeCombo = 5)

        // when
        updateBestSwipeComboUC(8)

        // then
        val slot = slot<Score>()
        verify(exactly = 1) { scoreManager.updateScore(capture(slot)) }
        assertEquals(8, slot.captured.bestSwipeCombo)
    }

    @Test
    fun `should not persist when combo does not beat the stored best`() {
        // given
        every { scoreManager.getScore() } returns Score(0, 0, null, null, emptyList(), bestSwipeCombo = 10)

        // when
        updateBestSwipeComboUC(10)
        updateBestSwipeComboUC(3)

        // then
        verify(exactly = 0) { scoreManager.updateScore(any()) }
    }
}
