package com.rafalskrzypczyk.firestore.config

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdsEnabledFlagTest {

    @Test
    fun `should disable ads for explicit disabling values`() {
        listOf("false", "FALSE", "False", " false ", "0", "no", "n", "off", "OFF").forEach {
            assertFalse("Wartość '$it' powinna wyłączać reklamy", parseAdsEnabledFlag(it))
        }
    }

    @Test
    fun `should keep ads enabled for explicit enabling values`() {
        listOf("true", "TRUE", " true ", "1", "yes", "on").forEach {
            assertTrue("Wartość '$it' powinna zostawiać reklamy włączone", parseAdsEnabledFlag(it))
        }
    }

    @Test
    fun `should fail open for missing or malformed values`() {
        listOf("", "   ", "flase", "disabled", "nie", "null", "-1").forEach {
            assertTrue("Wartość '$it' nie powinna wyłączać reklam", parseAdsEnabledFlag(it))
        }
    }
}
