package com.rafalskrzypczyk.paramedquiz.e2e.di

import com.rafalskrzypczyk.firestore.di.FirestoreModule
import com.rafalskrzypczyk.firestore.di.FirestoreModuleBinds
import com.rafalskrzypczyk.firestore.domain.FirestoreApi
import com.rafalskrzypczyk.paramedquiz.e2e.fakes.FakeFirestoreApi
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/**
 * Podmienia produkcyjne moduły Firestore (Firebase) na [FakeFirestoreApi] w testach.
 * Usuwa też `provideFirestore` (FirebaseFirestore) — nie jest już potrzebny, bo `FirestoreService`
 * nie jest konstruowany.
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [FirestoreModule::class, FirestoreModuleBinds::class]
)
object FakeFirestoreModule {

    @Provides
    @Singleton
    fun provideFakeFirestoreApi(): FakeFirestoreApi = FakeFirestoreApi()

    @Provides
    @Singleton
    fun provideFirestoreApi(fake: FakeFirestoreApi): FirestoreApi = fake
}
