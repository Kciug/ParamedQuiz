package com.rafalskrzypczyk.paramedquiz.e2e.di

import com.rafalskrzypczyk.analytics.di.AnalyticsModule
import com.rafalskrzypczyk.core.analytics.AnalyticsLogger
import com.rafalskrzypczyk.core.error.CrashReporter
import com.rafalskrzypczyk.core.error.NoOpCrashReporter
import com.rafalskrzypczyk.core.testing.RecordingAnalyticsLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/**
 * Podmienia całą warstwę analityki na atrapy.
 *
 * Obowiązkowy, nie opcjonalny: `AppErrorLogger` wstrzykuje [AnalyticsLogger], a harness wstrzykuje
 * `ErrorLogger`, więc bez tej podmiany testy zbudowałyby `FirebaseAnalyticsLogger`, a
 * `FirebaseAnalytics.getInstance()` pod `HiltTestApplication` bez zainicjalizowanego `FirebaseApp`
 * rzuca wyjątkiem.
 *
 * Testy mogą wstrzyknąć [RecordingAnalyticsLogger] wprost i asertować wysłane zdarzenia.
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AnalyticsModule::class]
)
object FakeAnalyticsModule {

    @Provides
    @Singleton
    fun provideRecordingAnalyticsLogger(): RecordingAnalyticsLogger = RecordingAnalyticsLogger()

    @Provides
    @Singleton
    fun provideAnalyticsLogger(recorder: RecordingAnalyticsLogger): AnalyticsLogger = recorder

    @Provides
    @Singleton
    fun provideCrashReporter(): CrashReporter = NoOpCrashReporter()
}
