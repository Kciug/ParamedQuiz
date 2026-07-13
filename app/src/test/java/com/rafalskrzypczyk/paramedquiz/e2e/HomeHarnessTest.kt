package com.rafalskrzypczyk.paramedquiz.e2e

import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ActivityScenario
import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import com.rafalskrzypczyk.core.testing.TestTags
import com.rafalskrzypczyk.paramedquiz.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import javax.inject.Inject

/**
 * Pełny przepływ E2E-HOME-01: powracający użytkownik (onboarding ukończony, regulamin zaakceptowany)
 * startuje wprost na ekranie głównym. Dowodzi, że **cały graf Hilt ekranu głównego** (najcięższy —
 * billing, premium, score, notyfikacje, reklamy) buduje się na fake'ach pod Robolectrikiem, bez Firebase.
 */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class, sdk = [34])
class HomeHarnessTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createEmptyComposeRule()

    @Inject
    lateinit var sharedPrefs: SharedPreferencesApi

    @Before
    fun setUp() {
        hiltRule.inject()
        // Stan „powracający użytkownik": onboarding za nami, aktualny regulamin (v1) zaakceptowany.
        sharedPrefs.setOnboardingStatus(true)
        sharedPrefs.setAcceptedTermsVersion(1)
    }

    @Test
    fun `returning user lands on home screen`() {
        // .use { } zamyka Activity po teście — bez tego pozostawiona MainActivity zaśmieca
        // współdzielony main looper Robolectrika i psuje kolejne testy w suicie.
        ActivityScenario.launch(MainActivity::class.java).use {
            composeRule.waitUntil(timeoutMillis = 5_000) {
                composeRule.onAllNodesWithTag(TestTags.HOME_ROOT).fetchSemanticsNodes().isNotEmpty()
            }

            composeRule.onNodeWithTag(TestTags.HOME_ROOT).assertExists()
        }
    }
}
