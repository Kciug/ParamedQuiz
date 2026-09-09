package com.rafalskrzypczyk.ads

import android.content.SharedPreferences
import com.rafalskrzypczyk.core.analytics.AnalyticsConsent
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TcfConsentReaderTest {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var reader: TcfConsentReader

    @Before
    fun setUp() {
        sharedPreferences = mockk(relaxed = true)
        reader = TcfConsentReader(sharedPreferences)
    }

    private fun givenTcf(gdprApplies: Int, purposeConsents: String?) {
        every { sharedPreferences.getInt("IABTCF_gdprApplies", any()) } returns gdprApplies
        every { sharedPreferences.getString("IABTCF_PurposeConsents", null) } returns purposeConsents
    }

    @Test
    fun `grants everything when gdpr does not apply`() {
        givenTcf(gdprApplies = 0, purposeConsents = null)

        val consent = reader.read(canRequestAds = false)

        assertEquals(AnalyticsConsent.granted(), consent)
    }

    @Test
    fun `falls back to canRequestAds when there is no tcf string yet`() {
        givenTcf(gdprApplies = 1, purposeConsents = null)

        assertTrue(reader.read(canRequestAds = true).analyticsStorage)
        assertFalse(reader.read(canRequestAds = false).analyticsStorage)
    }

    @Test
    fun `device storage consent drives analytics and ad storage`() {
        // Cel 1 zgoda, pozostale odmowa.
        givenTcf(gdprApplies = 1, purposeConsents = "1000000000")

        val consent = reader.read(canRequestAds = true)

        assertTrue(consent.analyticsStorage)
        assertTrue(consent.adStorage)
        assertFalse(consent.adUserData)
        assertFalse(consent.adPersonalization)
    }

    @Test
    fun `ad personalization requires both profiling and selection purposes`() {
        givenTcf(gdprApplies = 1, purposeConsents = "1010000000")
        assertFalse(reader.read(canRequestAds = true).adPersonalization)

        givenTcf(gdprApplies = 1, purposeConsents = "1011000000")
        assertTrue(reader.read(canRequestAds = true).adPersonalization)
    }

    @Test
    fun `ad user data requires ad measurement purpose`() {
        givenTcf(gdprApplies = 1, purposeConsents = "1000001000")

        assertTrue(reader.read(canRequestAds = true).adUserData)
    }

    @Test
    fun `denies everything when device storage is refused`() {
        givenTcf(gdprApplies = 1, purposeConsents = "0011001000")

        val consent = reader.read(canRequestAds = true)

        assertFalse(consent.analyticsStorage)
        assertFalse(consent.adStorage)
        assertFalse(consent.adUserData)
    }

    @Test
    fun `survives preferences holding an unexpected type`() {
        every { sharedPreferences.getInt("IABTCF_gdprApplies", any()) } throws ClassCastException()
        every { sharedPreferences.getString("IABTCF_PurposeConsents", null) } returns null

        assertFalse(reader.read(canRequestAds = false).analyticsStorage)
    }
}
