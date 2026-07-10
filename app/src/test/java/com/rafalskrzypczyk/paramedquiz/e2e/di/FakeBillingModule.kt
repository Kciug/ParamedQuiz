package com.rafalskrzypczyk.paramedquiz.e2e.di

import com.rafalskrzypczyk.billing.data.MockBillingRepository
import com.rafalskrzypczyk.billing.di.BillingModule
import com.rafalskrzypczyk.billing.domain.BillingRepository
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import com.rafalskrzypczyk.paramedquiz.e2e.fakes.FakePremiumStatusProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/**
 * Podmienia billing (Google Play) na [MockBillingRepository] + [FakePremiumStatusProvider].
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [BillingModule::class]
)
object FakeBillingModule {

    @Provides
    @Singleton
    fun provideMockBillingRepository(): MockBillingRepository = MockBillingRepository()

    @Provides
    @Singleton
    fun provideBillingRepository(mock: MockBillingRepository): BillingRepository = mock

    @Provides
    @Singleton
    fun provideFakePremiumStatusProvider(): FakePremiumStatusProvider = FakePremiumStatusProvider()

    @Provides
    @Singleton
    fun providePremiumStatusProvider(fake: FakePremiumStatusProvider): PremiumStatusProvider = fake
}
