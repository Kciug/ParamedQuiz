@file:Suppress("unused")

package com.rafalskrzypczyk.revisions.di

import com.rafalskrzypczyk.revisions.data.RevisionsRepositoryImpl
import com.rafalskrzypczyk.revisions.domain.RevisionsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RevisionsModule {

    @Binds
    @Singleton
    abstract fun bindRevisionsRepository(
        revisionsRepositoryImpl: RevisionsRepositoryImpl
    ): RevisionsRepository
}
