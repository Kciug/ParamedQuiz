package com.rafalskrzypczyk.analytics.di

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rafalskrzypczyk.analytics.BuildConfig
import com.rafalskrzypczyk.analytics.CrashlyticsCrashReporter
import com.rafalskrzypczyk.analytics.FirebaseAnalyticsLogger
import com.rafalskrzypczyk.core.analytics.AnalyticsBackend
import com.rafalskrzypczyk.core.analytics.AnalyticsCollectionGate
import com.rafalskrzypczyk.core.analytics.AnalyticsControls
import com.rafalskrzypczyk.core.analytics.AnalyticsLogger
import com.rafalskrzypczyk.core.analytics.ConsentGatedAnalyticsLogger
import com.rafalskrzypczyk.core.analytics.LogcatAnalyticsLogger
import com.rafalskrzypczyk.core.analytics.RecentAnalyticsEvents
import com.rafalskrzypczyk.core.error.CrashReporter
import com.rafalskrzypczyk.core.error.NoOpCrashReporter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Jedyne miejsce wiązania warstwy analityki — `core` celowo nie deklaruje domyślnych wiązań,
 * bo dwa `@Binds` dla tego samego typu w `SingletonComponent` to błąd kompilacji, a w projekcie
 * nie ma kwalifikatorów.
 *
 * Wybór implementacji odbywa się w runtime po [BuildConfig.BUILD_TYPE_NAME], a nie przez source
 * sety per build type: `assembleDebug` i `testDebugUnitTest` nigdy nie kompilują `src/release`,
 * więc implementacja produkcyjna wymykałaby się bramce weryfikacyjnej.
 *
 * Moduł **nie włącza zbierania** — robi to wyłącznie `AnalyticsConsentManager` po decyzji
 * użytkownika. Tutaj jest tylko jawne wyłączenie, żeby zamknąć okno między budową grafu
 * (pierwsza linia `Application.onCreate`) a zastosowaniem zapisanej zgody.
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
    fun provideAnalyticsBackend(@ApplicationContext context: Context): AnalyticsBackend {
        // Wyłączamy SDK także w debugu, gdzie wysyłką zajmuje się logcat: biblioteka jest
        // w aplikacji i bez tego zbierałaby zdarzenia automatyczne.
        FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(false)

        if (isDebugBuild) return LogcatAnalyticsLogger()

        return FirebaseAnalyticsLogger(FirebaseAnalytics.getInstance(context))
    }

    @Provides
    @Singleton
    fun provideAnalyticsControls(backend: AnalyticsBackend): AnalyticsControls = backend

    /** Wstrzykiwany wszędzie logger jest owinięty bramką — przed zgodą nie przepuszcza niczego. */
    @Provides
    @Singleton
    fun provideAnalyticsLogger(
        backend: AnalyticsBackend,
        gate: AnalyticsCollectionGate,
        recent: RecentAnalyticsEvents,
    ): AnalyticsLogger = ConsentGatedAnalyticsLogger(backend, gate, recent)

    @Provides
    @Singleton
    fun provideCrashReporter(): CrashReporter {
        if (isDebugBuild) return NoOpCrashReporter()

        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.isCrashlyticsCollectionEnabled = false
        return CrashlyticsCrashReporter(crashlytics)
    }
}
