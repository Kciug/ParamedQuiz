package com.rafalskrzypczyk.swipe_mode.di

import com.rafalskrzypczyk.swipe_mode.domain.SwipeModeRepository
import com.rafalskrzypczyk.swipe_mode.data.SwipeModeRepositoryImpl
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
    abstract fun bindSwipeModeRepository(
        swipeModeRepositoryImpl: SwipeModeRepositoryImpl
    ): SwipeModeRepository
}