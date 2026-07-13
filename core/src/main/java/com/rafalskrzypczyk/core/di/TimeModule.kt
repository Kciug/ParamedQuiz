package com.rafalskrzypczyk.core.di

import com.rafalskrzypczyk.core.utils.SystemTimeProvider
import com.rafalskrzypczyk.core.utils.TimeProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Osobny moduł dla [TimeProvider] — dzięki temu testy mogą podmienić samo źródło czasu
 * (`@TestInstallIn(replaces = [TimeModule::class])`) bez ruszania reszty bindingów core.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class TimeModule {
    @Binds
    @Singleton
    abstract fun bindTimeProvider(impl: SystemTimeProvider): TimeProvider
}
