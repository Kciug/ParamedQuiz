package com.rafalskrzypczyk.paramedquiz.e2e.di

import com.rafalskrzypczyk.core.di.TimeModule
import com.rafalskrzypczyk.core.utils.TimeProvider
import com.rafalskrzypczyk.paramedquiz.e2e.fakes.FakeTimeProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/** Podmienia systemowe źródło czasu na sterowalny [FakeTimeProvider]. */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [TimeModule::class]
)
object FakeTimeModule {

    @Provides
    @Singleton
    fun provideFakeTimeProvider(): FakeTimeProvider = FakeTimeProvider()

    @Provides
    @Singleton
    fun provideTimeProvider(fake: FakeTimeProvider): TimeProvider = fake
}
