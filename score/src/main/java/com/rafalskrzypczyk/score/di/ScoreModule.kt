package com.rafalskrzypczyk.score.di

import com.rafalskrzypczyk.score.domain.ScoreManager
import com.rafalskrzypczyk.score.di.ScoreRepositoryImpl
import com.rafalskrzypczyk.score.domain.ScoreStorage
import com.rafalskrzypczyk.score.di.ScoreStorageSharedPrefs
import com.rafalskrzypczyk.score.domain.ScoreRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ScoreModule {
    @Binds
    @Singleton
    abstract fun bindScoreRepository(
        mainModeRepositoryImpl: ScoreRepositoryImpl
    ): ScoreRepository

    @Binds
    @Singleton
    abstract fun bindScoreStorage(
        scoreStorageSharedPrefs: ScoreStorageSharedPrefs
    ): ScoreStorage
}

@Module
@InstallIn(SingletonComponent::class)
class ScoreModuleProvider {
    @Provides
    @Singleton
    fun provideScoreManager(
        repository: ScoreRepository,
        ioScope: CoroutineScope
    ): ScoreManager = ScoreManager(repository, ioScope)
}