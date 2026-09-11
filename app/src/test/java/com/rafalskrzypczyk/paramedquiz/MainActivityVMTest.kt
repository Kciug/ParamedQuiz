package com.rafalskrzypczyk.paramedquiz

import com.rafalskrzypczyk.core.analytics.AnalyticsConsentState
import com.rafalskrzypczyk.core.error.AppError
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import com.rafalskrzypczyk.firestore.domain.models.TermsOfServiceDTO
import com.rafalskrzypczyk.firestore.domain.models.TermsOfServiceStatus
import com.rafalskrzypczyk.firestore.domain.use_cases.ListenTermsOfServiceUpdatesUC
import com.rafalskrzypczyk.paramedquiz.navigation.MainMenu
import com.rafalskrzypczyk.paramedquiz.navigation.Onboarding
import com.rafalskrzypczyk.paramedquiz.navigation.PrivacyConsent
import com.rafalskrzypczyk.paramedquiz.navigation.TermsOfService
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Bramka zgody stoi na ścieżce prowadzącej do ekranu głównego, a nie na ścieżce regulaminu —
 * użytkownik, który zaakceptował już obowiązującą wersję regulaminu, nigdy przez tamten ekran
 * nie przechodzi, a mimo to musi zostać zapytany po aktualizacji.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainActivityVMTest {

    private lateinit var sharedPrefs: SharedPreferencesApi
    private lateinit var listenTermsUpdates: ListenTermsOfServiceUpdatesUC

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        sharedPrefs = mockk(relaxed = true)
        listenTermsUpdates = mockk(relaxed = true)

        every { sharedPrefs.getOnboardingStatus() } returns true
        every { sharedPrefs.getAcceptedTermsVersion() } returns 1
        every { listenTermsUpdates() } returns flowOf(TermsOfServiceStatus.Accepted)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = MainActivityVM(sharedPrefs, listenTermsUpdates)

    @Test
    fun `undecided consent sends the user to the consent screen`() = runTest {
        every { sharedPrefs.getAnalyticsConsent() } returns AnalyticsConsentState.UNDECIDED

        val viewModel = createViewModel()

        assertEquals(PrivacyConsent, viewModel.state.value.startDestination)
    }

    @Test
    fun `decided consent goes straight to the home screen`() = runTest {
        every { sharedPrefs.getAnalyticsConsent() } returns AnalyticsConsentState.DENIED

        val viewModel = createViewModel()

        assertEquals(MainMenu, viewModel.state.value.startDestination)
    }

    @Test
    fun `terms come first even when the consent is still undecided`() = runTest {
        every { sharedPrefs.getAcceptedTermsVersion() } returns -1
        every { sharedPrefs.getAnalyticsConsent() } returns AnalyticsConsentState.UNDECIDED

        val viewModel = createViewModel()

        assertTrue(viewModel.state.value.startDestination is TermsOfService)
    }

    @Test
    fun `terms needing acceptance win over the consent gate`() = runTest {
        every { listenTermsUpdates() } returns flowOf(
            TermsOfServiceStatus.NeedsAcceptance(TermsOfServiceDTO(version = 2))
        )
        every { sharedPrefs.getAnalyticsConsent() } returns AnalyticsConsentState.UNDECIDED

        val viewModel = createViewModel()

        assertTrue(viewModel.state.value.startDestination is TermsOfService)
    }

    @Test
    fun `onboarding still comes before anything else`() = runTest {
        every { sharedPrefs.getOnboardingStatus() } returns false
        every { sharedPrefs.getAnalyticsConsent() } returns AnalyticsConsentState.UNDECIDED

        val viewModel = createViewModel()

        assertEquals(Onboarding, viewModel.state.value.startDestination)
    }

    @Test
    fun `a failed terms lookup does not skip the consent gate`() = runTest {
        // Blad i timeout wpadaja w te sama galaz else, ktora tez musi przejsc przez bramke.
        every { listenTermsUpdates() } returns flowOf(TermsOfServiceStatus.Error(AppError.NoNetwork))
        every { sharedPrefs.getAnalyticsConsent() } returns AnalyticsConsentState.UNDECIDED

        val viewModel = createViewModel()

        assertEquals(PrivacyConsent, viewModel.state.value.startDestination)
    }
}
