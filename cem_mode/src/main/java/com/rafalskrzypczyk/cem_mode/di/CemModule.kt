package com.rafalskrzypczyk.cem_mode.di

import com.rafalskrzypczyk.cem_mode.data.CemRepositoryImpl
import com.rafalskrzypczyk.cem_mode.domain.CemRepository
import com.rafalskrzypczyk.cem_mode.domain.use_cases.CemUseCases
import com.rafalskrzypczyk.cem_mode.domain.use_cases.GetCemCategoriesUseCase
import com.rafalskrzypczyk.cem_mode.domain.use_cases.GetUpdatedCemCategoriesUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CemModule {

    @Binds
    @Singleton
    abstract fun bindCemRepository(impl: CemRepositoryImpl): CemRepository

    companion object {
        @Provides
        @Singleton
        fun provideCemUseCases(
            getCemCategories: GetCemCategoriesUseCase,
            getUpdatedCemCategories: GetUpdatedCemCategoriesUseCase
        ): CemUseCases = CemUseCases(
            getCemCategories = getCemCategories,
            getUpdatedCemCategories = getUpdatedCemCategories
        )
    }
}
