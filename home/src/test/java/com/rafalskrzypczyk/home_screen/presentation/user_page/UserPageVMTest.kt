package com.rafalskrzypczyk.home_screen.presentation.user_page

import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import com.rafalskrzypczyk.core.domain.config.GameplayConfig
import com.rafalskrzypczyk.core.domain.config.GameplayConfigProvider
import com.rafalskrzypczyk.home_screen.domain.user_page.UserPageUseCases
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserPageVMTest {

    private lateinit var useCases: UserPageUseCases
    private lateinit var premiumStatusProvider: PremiumStatusProvider
    private lateinit var gameplayConfig: GameplayConfigProvider
    private lateinit var viewModel: UserPageVM

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        useCases = mockk(relaxed = true)
        premiumStatusProvider = mockk(relaxed = true)
        gameplayConfig = mockk(relaxed = true)

        every { useCases.getUser() } returns null
        every { useCases.getUserScore() } returns emptyFlow()
        every { premiumStatusProvider.ownedProductIds } returns flowOf(emptySet())

        viewModel = UserPageVM(useCases, premiumStatusProvider, gameplayConfig)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state falls back to in-app defaults before any refresh`() {
        assertEquals(
            GameplayConfig.DEFAULT.firstCorrectPoints,
            viewModel.state.value.firstCorrectPoints
        )
        assertEquals(
            GameplayConfig.DEFAULT.correctPoints,
            viewModel.state.value.correctPoints
        )
    }

    @Test
    fun `RefreshUserData takes point values from GameplayConfigProvider`() = runTest {
        every { gameplayConfig.firstCorrectPoints() } returns 555
        every { gameplayConfig.correctPoints() } returns 42

        viewModel.onEvent(UserPageUIEvents.RefreshUserData)

        assertEquals(555, viewModel.state.value.firstCorrectPoints)
        assertEquals(42, viewModel.state.value.correctPoints)
    }
}
