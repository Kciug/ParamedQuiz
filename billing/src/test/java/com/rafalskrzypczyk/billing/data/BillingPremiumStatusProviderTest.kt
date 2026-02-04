package com.rafalskrzypczyk.billing.data

import com.rafalskrzypczyk.billing.domain.AppPurchase
import com.rafalskrzypczyk.billing.domain.BillingIds
import com.rafalskrzypczyk.billing.domain.BillingRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BillingPremiumStatusProviderTest {

    private lateinit var billingRepository: BillingRepository
    private lateinit var premiumStatusProvider: BillingPremiumStatusProvider

    @Before
    fun setup() {
        billingRepository = mockk(relaxed = true)
        premiumStatusProvider = BillingPremiumStatusProvider(billingRepository)
    }

    @Test
    fun `isAdsFree returns true when purchases list is not empty`() = runTest {
        val purchase = AppPurchase(products = emptyList(), isPurchased = true)
        every { billingRepository.purchases } returns flowOf(listOf(purchase))

        premiumStatusProvider.isAdsFree.collect { isAdsFree ->
            assertTrue(isAdsFree)
        }
    }

    @Test
    fun `isAdsFree returns false when purchases list is empty`() = runTest {
        every { billingRepository.purchases } returns flowOf(emptyList())

        premiumStatusProvider.isAdsFree.collect { isAdsFree ->
            assertFalse(isAdsFree)
        }
    }

    @Test
    fun `hasAccessTo returns true when user owns full package`() = runTest {
        val purchase = AppPurchase(products = listOf(BillingIds.ID_FULL_PACKAGE), isPurchased = true)
        every { billingRepository.purchases } returns flowOf(listOf(purchase))

        premiumStatusProvider.hasAccessTo("some_random_id").collect { hasAccess ->
            assertTrue(hasAccess)
        }
    }

    @Test
    fun `hasAccessTo returns true when user owns specific product`() = runTest {
        val targetId = "target_product_id"
        val purchase = AppPurchase(products = listOf(targetId), isPurchased = true)
        every { billingRepository.purchases } returns flowOf(listOf(purchase))

        premiumStatusProvider.hasAccessTo(targetId).collect { hasAccess ->
            assertTrue(hasAccess)
        }
    }

    @Test
    fun `hasAccessTo returns false when user owns nothing`() = runTest {
        every { billingRepository.purchases } returns flowOf(emptyList())

        premiumStatusProvider.hasAccessTo("some_id").collect { hasAccess ->
            assertFalse(hasAccess)
        }
    }

    @Test
    fun `hasAccessTo returns false when user owns different product`() = runTest {
        val purchase = AppPurchase(products = listOf("other_id"), isPurchased = true)
        every { billingRepository.purchases } returns flowOf(listOf(purchase))

        premiumStatusProvider.hasAccessTo("target_id").collect { hasAccess ->
            assertFalse(hasAccess)
        }
    }
}
