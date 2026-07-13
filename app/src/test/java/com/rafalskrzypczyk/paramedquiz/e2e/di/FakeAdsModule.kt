package com.rafalskrzypczyk.paramedquiz.e2e.di

import com.rafalskrzypczyk.ads.di.AdsModule
import com.rafalskrzypczyk.core.ads.AdManager
import com.rafalskrzypczyk.paramedquiz.e2e.fakes.NoOpAdManager
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/** Podmienia AdMob na [NoOpAdManager] — brak reklam w testach. */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AdsModule::class]
)
object FakeAdsModule {

    @Provides
    @Singleton
    fun provideAdManager(): AdManager = NoOpAdManager()
}
