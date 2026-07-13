package com.rafalskrzypczyk.paramedquiz.e2e.di

import com.rafalskrzypczyk.main_mode.di.MainModeModule
import com.rafalskrzypczyk.main_mode.domain.MainModeRepository
import com.rafalskrzypczyk.paramedquiz.e2e.fakes.FakeMainModeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/**
 * Podmienia cache'ujące `MainModeRepositoryImpl` na [FakeMainModeRepository] (bez cache),
 * żeby seed pytań/kategorii per test zawsze był respektowany (odporność na współdzielenie singletonów).
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [MainModeModule::class]
)
object FakeMainModeModule {

    @Provides
    @Singleton
    fun provideMainModeRepository(fake: FakeMainModeRepository): MainModeRepository = fake
}
