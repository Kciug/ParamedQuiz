@file:Suppress("unused")

package com.rafalskrzypczyk.translation_mode.di

import com.rafalskrzypczyk.translation_mode.data.TranslationRepositoryImpl
import com.rafalskrzypczyk.translation_mode.domain.repository.TranslationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TranslationModeModule {
    
    @Binds
    @Singleton
    abstract fun bindTranslationRepository(
        translationRepositoryImpl: TranslationRepositoryImpl
    ): TranslationRepository
}