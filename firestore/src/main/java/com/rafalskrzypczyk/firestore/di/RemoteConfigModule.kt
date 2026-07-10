@file:Suppress("unused")

package com.rafalskrzypczyk.firestore.di

import com.rafalskrzypczyk.core.domain.config.GameplayConfigProvider
import com.rafalskrzypczyk.firestore.config.RemoteConfigGameplayProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RemoteConfigModule {
    @Binds
    @Singleton
    abstract fun bindGameplayConfigProvider(
        impl: RemoteConfigGameplayProvider
    ): GameplayConfigProvider
}
