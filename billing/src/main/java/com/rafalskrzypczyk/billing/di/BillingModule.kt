@file:Suppress("unused")

package com.rafalskrzypczyk.billing.di

import com.rafalskrzypczyk.billing.data.BillingRepositoryImpl
import com.rafalskrzypczyk.billing.data.MockBillingRepository
import com.rafalskrzypczyk.billing.domain.BillingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BillingModule {

    private const val USE_MOCK_BILLING = true

    @Provides
    @Singleton
    fun provideBillingRepository(
        realImpl: BillingRepositoryImpl,
        mockImpl: MockBillingRepository
    ): BillingRepository {
        return if (USE_MOCK_BILLING) mockImpl else realImpl
    }

    @Provides
    @Singleton
    fun providePremiumStatusProvider(
        provider: com.rafalskrzypczyk.billing.data.BillingPremiumStatusProvider
    ): com.rafalskrzypczyk.core.billing.PremiumStatusProvider {
        return provider
    }
}