@file:Suppress("unused")

package com.rafalskrzypczyk.billing.di

import com.rafalskrzypczyk.billing.data.BillingRepositoryImpl
import com.rafalskrzypczyk.billing.domain.BillingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BillingModule {

    @Binds
    @Singleton
    abstract fun bindBillingRepository(
        billingRepositoryImpl: BillingRepositoryImpl
    ): BillingRepository
}