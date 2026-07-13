package com.rafalskrzypczyk.paramedquiz.e2e.di

import com.rafalskrzypczyk.auth.di.AuthModule
import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.paramedquiz.e2e.fakes.FakeAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/**
 * Podmienia moduły auth (Firebase Auth) na [FakeAuthRepository].
 * `FirestoreModuleBinds` to nazwa klasy bindującej `AuthRepository` w pakiecie auth.di.
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AuthModule::class, com.rafalskrzypczyk.auth.di.FirestoreModuleBinds::class]
)
object FakeAuthModule {

    @Provides
    @Singleton
    fun provideFakeAuthRepository(): FakeAuthRepository = FakeAuthRepository()

    @Provides
    @Singleton
    fun provideAuthRepository(fake: FakeAuthRepository): AuthRepository = fake
}
