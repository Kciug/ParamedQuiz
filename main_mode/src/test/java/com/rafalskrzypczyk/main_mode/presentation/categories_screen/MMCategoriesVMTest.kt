package com.rafalskrzypczyk.main_mode.presentation.categories_screen

import com.rafalskrzypczyk.billing.domain.BillingRepository
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import com.rafalskrzypczyk.main_mode.domain.quiz_categories.MMCategoriesUseCases
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
class MMCategoriesVMTest {

    private lateinit var useCases: MMCategoriesUseCases
    private lateinit var billingRepository: BillingRepository
    private lateinit var premiumStatusProvider: PremiumStatusProvider
    private lateinit var viewModel: MMCategoriesVM

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        
        useCases = mockk(relaxed = true)
        billingRepository = mockk(relaxed = true)
        premiumStatusProvider = mockk(relaxed = true)
        
        every { billingRepository.availableProducts } returns flowOf(emptyList())
        
        viewModel = MMCategoriesVM(useCases, billingRepository, premiumStatusProvider)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `OpenPurchaseDialog triggers queryProducts`() = runTest {
        val category = CategoryUIM(1, "Title", "Desc", "10", false, 0f)
        
        viewModel.onEvent(MMCategoriesUIEvents.OpenPurchaseDialog(category))
        
        coVerify { billingRepository.queryProducts(listOf("category_1")) }
    }
}
