package com.rafalskrzypczyk.billing.data

import com.android.billingclient.api.Purchase
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
        val purchase = mockk<Purchase>()
        every { purchase.purchaseState } returns Purchase.PurchaseState.PURCHASED
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
        val purchase = mockk<Purchase>()
        every { purchase.purchaseState } returns Purchase.PurchaseState.PURCHASED
        every { purchase.products } returns listOf(BillingIds.ID_FULL_PACKAGE)
        every { billingRepository.purchases } returns flowOf(listOf(purchase))

        premiumStatusProvider.hasAccessTo("some_random_id").collect { hasAccess ->
            assertTrue(hasAccess)
        }
    }

    @Test
    fun `hasAccessTo returns true when user owns specific product`() = runTest {
        val targetId = "target_product_id"
        val purchase = mockk<Purchase>()
        every { purchase.purchaseState } returns Purchase.PurchaseState.PURCHASED
        every { purchase.products } returns listOf(targetId)
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
        val purchase = mockk<Purchase>()
        every { purchase.purchaseState } returns Purchase.PurchaseState.PURCHASED
        every { purchase.products } returns listOf("other_id")
        every { billingRepository.purchases } returns flowOf(listOf(purchase))

        premiumStatusProvider.hasAccessTo("target_id").collect { hasAccess ->
            assertFalse(hasAccess)
        }
    }
}
