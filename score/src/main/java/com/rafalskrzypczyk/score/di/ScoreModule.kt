package com.rafalskrzypczyk.score.di

import com.rafalskrzypczyk.core.user_management.UserManager
import com.rafalskrzypczyk.score.ScoreManager
import com.rafalskrzypczyk.score.ScoreRepositoryImpl
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
}

@Module
@InstallIn(SingletonComponent::class)
class ScoreModuleProvider {
    @Provides
    @Singleton
    fun provideScoreManager(
        repository: ScoreRepository,
        userManager: UserManager,
        ioScope: CoroutineScope
    ): ScoreManager = ScoreManager(repository, userManager, ioScope)
}