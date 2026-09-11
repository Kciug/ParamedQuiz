package com.rafalskrzypczyk.core.analytics

import com.rafalskrzypczyk.core.error.CrashReporter
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsConsentManagerTest {

    private lateinit var sharedPreferences: SharedPreferencesApi
    private lateinit var controls: AnalyticsControls
    private lateinit var crashReporter: CrashReporter
    private lateinit var gate: AnalyticsCollectionGate

    @Before
    fun setUp() {
        sharedPreferences = mockk(relaxed = true)
        controls = mockk(relaxed = true)
        crashReporter = mockk(relaxed = true)
        gate = AnalyticsCollectionGate()
    }

    private fun createManager(stored: AnalyticsConsentState): AnalyticsConsentManager {
        every { sharedPreferences.getAnalyticsConsent() } returns stored
        return AnalyticsConsentManager(sharedPreferences, controls, crashReporter, gate)
    }

    @Test
    fun `undecided start disables collection and never opens the gate`() {
        val manager = createManager(AnalyticsConsentState.UNDECIDED)

        manager.apply()

        verify { controls.setCollectionEnabled(false) }
        verify { crashReporter.setCollectionEnabled(false) }
        verify(exactly = 0) { controls.setCollectionEnabled(true) }
        assertFalse(gate.isOpen)
    }

    @Test
    fun `undecided start also clears whatever the sdk collected before our code ran`() {
        val manager = createManager(AnalyticsConsentState.UNDECIDED)

        manager.apply()

        verify { controls.resetAnalyticsData() }
        verify { crashReporter.deleteUnsentReports() }
    }

    @Test
    fun `granting sends the consent before enabling collection`() {
        val manager = createManager(AnalyticsConsentState.UNDECIDED)

        manager.grant()

        verifyOrder {
            controls.setConsent(match { it.analyticsStorage })
            controls.setCollectionEnabled(true)
        }
        verify { sharedPreferences.setAnalyticsConsent(AnalyticsConsentState.GRANTED) }
        assertTrue(gate.isOpen)
    }

    @Test
    fun `withdrawing disables collection and then purges the data`() {
        val manager = createManager(AnalyticsConsentState.GRANTED)

        manager.withdraw()

        verifyOrder {
            controls.setCollectionEnabled(false)
            controls.resetAnalyticsData()
        }
        verify { crashReporter.deleteUnsentReports() }
        verify { sharedPreferences.setAnalyticsConsent(AnalyticsConsentState.DENIED) }
        assertFalse(gate.isOpen)
    }

    @Test
    fun `ad consent from the ump form does not overwrite a granted analytics decision`() {
        val manager = createManager(AnalyticsConsentState.GRANTED)
        manager.apply()

        manager.updateAdConsent(AdConsent.denied())

        verify {
            controls.setConsent(match { it.analyticsStorage && !it.adStorage })
        }
        assertTrue(gate.isOpen)
    }

    @Test
    fun `ad consent from the ump form does not grant analytics on its own`() {
        val manager = createManager(AnalyticsConsentState.DENIED)
        manager.apply()

        manager.updateAdConsent(AdConsent.granted())

        verify {
            controls.setConsent(match { !it.analyticsStorage && it.adStorage })
        }
        verify(exactly = 0) { controls.setCollectionEnabled(true) }
        assertFalse(gate.isOpen)
    }

    @Test
    fun `seeded ad consent survives into the emitted payload`() {
        val manager = createManager(AnalyticsConsentState.GRANTED)

        manager.apply(AdConsent(adStorage = true, adUserData = false, adPersonalization = true))

        verify {
            controls.setConsent(
                AnalyticsConsent(
                    analyticsStorage = true,
                    adStorage = true,
                    adUserData = false,
                    adPersonalization = true,
                )
            )
        }
    }

    /**
     * Kolektor `state` biegnie na Dispatchers.Main.immediate i wznawia sie synchronicznie w setterze
     * `_state.value`. Gdyby stan byl publikowany przed otwarciem bramki, wlasciwosci uzytkownika
     * ustawiane w reakcji na GRANTED trafialyby w bramke jeszcze zamknieta i przepadaly.
     */
    @Test
    fun `the gate is already open when the state emits granted`() {
        val manager = createManager(AnalyticsConsentState.UNDECIDED)
        val gateAtEmission = mutableListOf<Pair<AnalyticsConsentState, Boolean>>()

        val job = CoroutineScope(UnconfinedTestDispatcher()).launch {
            manager.state.collect { gateAtEmission += it to gate.isOpen }
        }
        manager.grant()
        job.cancel()

        assertEquals(
            listOf(AnalyticsConsentState.UNDECIDED to false, AnalyticsConsentState.GRANTED to true),
            gateAtEmission,
        )
    }

    @Test
    fun `state flow exposes the stored decision and every change`() {
        val manager = createManager(AnalyticsConsentState.UNDECIDED)
        assertEquals(AnalyticsConsentState.UNDECIDED, manager.state.value)

        manager.grant()
        assertEquals(AnalyticsConsentState.GRANTED, manager.state.value)

        manager.withdraw()
        assertEquals(AnalyticsConsentState.DENIED, manager.state.value)

        manager.reset()
        assertEquals(AnalyticsConsentState.UNDECIDED, manager.state.value)
    }
}
