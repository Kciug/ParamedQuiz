package com.rafalskrzypczyk.core.di

import com.rafalskrzypczyk.core.error.ErrorLogger
import com.rafalskrzypczyk.core.error.LogcatErrorLogger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Punkt podmiany implementacji diagnostyki błędów.
 *
 * Wejście Crashlytics sprowadza się do zamiany [LogcatErrorLogger] na implementację
 * raportującą, bez zmian w warstwach data, domain i presentation.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ErrorModuleBinds {
    @Binds
    @Singleton
    abstract fun bindErrorLogger(logger: LogcatErrorLogger): ErrorLogger
}
