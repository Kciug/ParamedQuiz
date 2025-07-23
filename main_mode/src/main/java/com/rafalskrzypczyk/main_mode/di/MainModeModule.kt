@file:Suppress("unused")

package com.rafalskrzypczyk.main_mode.di

import com.rafalskrzypczyk.main_mode.data.MainModeRepositoryImpl
import com.rafalskrzypczyk.main_mode.domain.MainModeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MainModeModule {
    @Binds
    @Singleton
    abstract fun bindMainModeRepository(
        mainModeRepositoryImpl: MainModeRepositoryImpl
    ): MainModeRepository
}