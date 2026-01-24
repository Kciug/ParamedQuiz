@file:Suppress("unused")

package com.rafalskrzypczyk.ads.di

import com.rafalskrzypczyk.ads.AdManagerImpl
import com.rafalskrzypczyk.core.ads.AdManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AdsModule {

    @Binds
    @Singleton
    abstract fun bindAdManager(
        adManagerImpl: AdManagerImpl
    ): AdManager
}
