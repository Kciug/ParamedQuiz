package com.rafalskrzypczyk.analytics.di

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rafalskrzypczyk.analytics.BuildConfig
import com.rafalskrzypczyk.analytics.CrashlyticsCrashReporter
import com.rafalskrzypczyk.analytics.FirebaseAnalyticsLogger
import com.rafalskrzypczyk.core.analytics.AnalyticsLogger
import com.rafalskrzypczyk.core.analytics.AnalyticsUserProperty
import com.rafalskrzypczyk.core.analytics.LogcatAnalyticsLogger
import com.rafalskrzypczyk.core.error.CrashReporter
import com.rafalskrzypczyk.core.error.NoOpCrashReporter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Jedyne miejsce wiązania [AnalyticsLogger] i [CrashReporter] w całej aplikacji — `core` celowo
 * nie deklaruje domyślnego wiązania, bo dwa `@Binds` dla tego samego typu w `SingletonComponent`
 * to błąd kompilacji, a w projekcie nie ma kwalifikatorów.
 *
 * Wybór implementacji odbywa się w runtime po [BuildConfig.BUILD_TYPE_NAME], a nie przez source
 * sety per build type: `assembleDebug` i `testDebugUnitTest` nigdy nie kompilują `src/release`,
 * więc implementacja produkcyjna wymykałaby się bramce weryfikacyjnej.
 *
 * W testach cały moduł jest podmieniany przez `FakeAnalyticsModule` (`@TestInstallIn`).
 */
@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    private const val BUILD_TYPE_DEBUG = "debug"

    private val isDebugBuild: Boolean get() = BuildConfig.BUILD_TYPE_NAME == BUILD_TYPE_DEBUG

    @Provides
    @Singleton
    fun provideAnalyticsLogger(@ApplicationContext context: Context): AnalyticsLogger {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        firebaseAnalytics.setAnalyticsCollectionEnabled(!isDebugBuild)

        val logger =
            if (isDebugBuild) LogcatAnalyticsLogger() else FirebaseAnalyticsLogger(firebaseAnalytics)

        // Wariant staging ma ten sam applicationId co release — bez tej właściwości ruch testowy
        // z internal tracka zanieczyściłby dane produkcyjne.
        logger.setUserProperty(AnalyticsUserProperty.BUILD_TYPE, BuildConfig.BUILD_TYPE_NAME)
        return logger
    }

    @Provides
    @Singleton
    fun provideCrashReporter(): CrashReporter {
        if (isDebugBuild) return NoOpCrashReporter()

        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.isCrashlyticsCollectionEnabled = true
        crashlytics.setCustomKey(AnalyticsUserProperty.BUILD_TYPE.propertyName, BuildConfig.BUILD_TYPE_NAME)
        return CrashlyticsCrashReporter(crashlytics)
    }
}
