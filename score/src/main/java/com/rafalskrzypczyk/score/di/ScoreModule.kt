package com.rafalskrzypczyk.score.di

import com.rafalskrzypczyk.score.ScoreRepositoryImpl
import com.rafalskrzypczyk.score.domain.ScoreRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ScoreModule {
    @Binds
    @Singleton
    abstract fun bindScoreRepository(
        mainModeRepositoryImpl: ScoreRepositoryImpl
    ): ScoreRepository
}