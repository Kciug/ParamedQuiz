package com.rafalskrzypczyk.billing.data

import android.content.Context
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.QueryPurchasesParams
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BillingDataSourceTest {

    private lateinit var billingDataSource: BillingDataSource
    private val context: Context = mockk(relaxed = true)
    private val billingClient: BillingClient = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0

        val provider = mockk<BillingClientProvider>()
        every { provider.create(any(), any()) } returns billingClient
        billingDataSource = BillingDataSource(context, testScope, provider)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `startConnection starts billing connection`() = runTest {
        // Given
        every { billingClient.isReady } returns false

        // When
        billingDataSource.startConnection()

        // Then
        verify { billingClient.startConnection(any()) }
    }

    @Test
    fun `onBillingSetupFinished OK refreshes purchases`() = runTest {
        // Given
        val listenerSlot = slot<BillingClientStateListener>()
        every { billingClient.isReady } returns false
        every { billingClient.startConnection(capture(listenerSlot)) } answers {
            listenerSlot.captured.onBillingSetupFinished(
                BillingResult.newBuilder().setResponseCode(BillingClient.BillingResponseCode.OK).build()
            )
        }
        
        // When
        billingDataSource.startConnection()

        // Then
        verify { billingClient.queryPurchasesAsync(any<QueryPurchasesParams>(), any<PurchasesResponseListener>()) }
    }

    @Test
    fun `onBillingSetupFinished Error does not refresh purchases`() = runTest {
        // Given
        val listenerSlot = slot<BillingClientStateListener>()
        every { billingClient.isReady } returns false
        every { billingClient.startConnection(capture(listenerSlot)) } answers {
            listenerSlot.captured.onBillingSetupFinished(
                BillingResult.newBuilder().setResponseCode(BillingClient.BillingResponseCode.ERROR).build()
            )
        }

        // When
        billingDataSource.startConnection()

        // Then
        verify(exactly = 0) { billingClient.queryPurchasesAsync(any<QueryPurchasesParams>(), any<PurchasesResponseListener>()) }
    }
}