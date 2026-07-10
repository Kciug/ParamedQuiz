package com.rafalskrzypczyk.paramedquiz.e2e.di

import com.rafalskrzypczyk.core.domain.config.GameplayConfigProvider
import com.rafalskrzypczyk.firestore.di.RemoteConfigModule
import com.rafalskrzypczyk.paramedquiz.e2e.fakes.FakeGameplayConfigProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/** Podmienia Remote Config (Firebase) na [FakeGameplayConfigProvider] z wartościami domyślnymi. */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RemoteConfigModule::class]
)
object FakeRemoteConfigModule {

    @Provides
    @Singleton
    fun provideFakeGameplayConfigProvider(): FakeGameplayConfigProvider = FakeGameplayConfigProvider()

    @Provides
    @Singleton
    fun provideGameplayConfigProvider(fake: FakeGameplayConfigProvider): GameplayConfigProvider = fake
}
