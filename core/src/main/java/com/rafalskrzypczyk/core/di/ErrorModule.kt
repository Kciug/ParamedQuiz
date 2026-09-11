package com.rafalskrzypczyk.core.di

import com.rafalskrzypczyk.core.error.AppErrorLogger
import com.rafalskrzypczyk.core.error.ErrorLogger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Punkt podmiany implementacji diagnostyki błędów.
 *
 * [AppErrorLogger] rozsyła każdy błąd w trzy miejsca: logcat, Crashlytics i zdarzenie `app_error`.
 * Podmiana implementacji nie wymaga zmian w warstwach data, domain i presentation.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ErrorModuleBinds {
    @Binds
    @Singleton
    abstract fun bindErrorLogger(logger: AppErrorLogger): ErrorLogger
}
