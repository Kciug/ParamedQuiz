package com.rafalskrzypczyk.core.di

import android.content.Context
import android.content.SharedPreferences
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesService
import com.rafalskrzypczyk.core.user_management.UserManager
import com.rafalskrzypczyk.core.user_management.UserManagerImpl
import com.rafalskrzypczyk.core.utils.FirebaseError
import com.rafalskrzypczyk.core.utils.ResourceProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoreModule {
    @Provides
    @Singleton
    fun provideResourceProvider(@ApplicationContext context: Context): ResourceProvider =
        ResourceProvider(context)

    @Provides
    @Singleton
    fun provideFirebaseErrorHandler(resourceProvider: ResourceProvider): FirebaseError =
        FirebaseError(resourceProvider)

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(
            context.packageName + "_preferences",
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun provideIOCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreModuleBinds {
    @Binds
    @Singleton
    abstract fun bindSharedPreferencesApi(sharedPreferencesService: SharedPreferencesService): SharedPreferencesApi

    @Binds
    @Singleton
    abstract fun bindUserManager(manager: UserManagerImpl): UserManager
}