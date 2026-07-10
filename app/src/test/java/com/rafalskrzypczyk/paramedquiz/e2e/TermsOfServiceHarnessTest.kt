package com.rafalskrzypczyk.paramedquiz.e2e

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.rafalskrzypczyk.core.testing.TestTags
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.home_screen.presentation.terms_of_service.TermsOfServiceScreen
import com.rafalskrzypczyk.home_screen.presentation.terms_of_service.TermsOfServiceUseCases
import com.rafalskrzypczyk.home_screen.presentation.terms_of_service.TermsOfServiceVM
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import javax.inject.Inject

/**
 * Test referencyjny harnessu E2E (Porcja 2). Dowodzi, że działa cały stos:
 * Gradle (Robolectric) + graf Hilt z fake'iem zamiast Firebase ([FakeFirestoreApi]) + Compose UI test.
 *
 * Scenariusz: obowiązkowy regulamin ładuje się z fake backendu i daje się zaakceptować.
 * Pełny przepływ onboarding→regulamin→home (E2E-ONBOARDING-01) dokładamy w Porcji 3.
 */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class, sdk = [34])
class TermsOfServiceHarnessTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @Inject
    lateinit var useCases: TermsOfServiceUseCases

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun `mandatory terms load from fake backend and can be accepted`() {
        var accepted = false
        val viewModel = TermsOfServiceVM(useCases)

        composeRule.setContent {
            val state = viewModel.state.collectAsState().value
            ParamedQuizTheme {
                TermsOfServiceScreen(
                    state = state,
                    isMandatory = true,
                    onEvent = viewModel::onEvent,
                    onAccepted = { accepted = true },
                    onNavigateBack = {}
                )
            }
        }

        // Treść regulaminu (zaseedowana w fake'u) jest wyświetlona.
        composeRule.onNodeWithText("REGULAMIN_TESTOWY", substring = true).assertExists()

        // Akceptacja przez stabilny test-tag → callback onAccepted odpalony.
        composeRule.onNodeWithTag(TestTags.TOS_ACCEPT_BUTTON).performClick()
        composeRule.waitForIdle()

        assertTrue("Oczekiwano wywołania onAccepted po akceptacji regulaminu", accepted)
    }
}
