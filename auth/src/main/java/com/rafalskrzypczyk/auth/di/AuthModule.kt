package com.rafalskrzypczyk.auth.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.rafalskrzypczyk.auth.data.AuthRepositoryImpl
import com.rafalskrzypczyk.auth.data.GoogleCredentialsProviderImpl
import com.rafalskrzypczyk.auth.domain.AuthRepository
import com.rafalskrzypczyk.auth.domain.GoogleCredentialsProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AuthModule {
    @Provides
    @Singleton
    fun provideFBAuth(): FirebaseAuth = Firebase.auth
}

@InstallIn(SingletonComponent::class)
@Module
abstract class AuthModuleBinds {
    @Singleton
    @Binds
    abstract fun bindAuthApi(authImpl: AuthRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    abstract fun bindGoogleCredentialsProvider(provider: GoogleCredentialsProviderImpl): GoogleCredentialsProvider
}
