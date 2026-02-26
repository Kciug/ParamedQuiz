package com.rafalskrzypczyk.home_screen.presentation.home_page

import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.billing.domain.BillingRepository
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import com.rafalskrzypczyk.home_screen.domain.HomeScreenUseCases
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenVMTest {

    private lateinit var useCases: HomeScreenUseCases
    private lateinit var premiumStatusProvider: PremiumStatusProvider
    private lateinit var billingRepository: BillingRepository
    private lateinit var viewModel: HomeScreenVM

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        
        useCases = mockk(relaxed = true)
        premiumStatusProvider = mockk(relaxed = true)
        billingRepository = mockk(relaxed = true)
        
        every { billingRepository.availableProducts } returns flowOf(emptyList())
        every { useCases.getUserScore() } returns flowOf(mockk(relaxed = true))
        every { useCases.getUserData() } returns flowOf(mockk(relaxed = true))
        every { premiumStatusProvider.ownedProductIds } returns flowOf(emptySet())
        
        viewModel = HomeScreenVM(useCases, premiumStatusProvider, billingRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `OpenTranslationModePurchaseSheet triggers queryProducts`() = runTest {
        viewModel.onEvent(HomeUIEvents.OpenTranslationModePurchaseSheet)
        
        coVerify { billingRepository.queryProducts(listOf(BillingIds.ID_TRANSLATION_MODE)) }
    }
}
