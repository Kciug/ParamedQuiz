package com.rafalskrzypczyk.paramedquiz.e2e

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.testing.TestTags
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.revisions.domain.models.RevisionCategory
import com.rafalskrzypczyk.revisions.domain.models.RevisionCriterion
import com.rafalskrzypczyk.revisions.presentation.RevisionsConfigScreen
import com.rafalskrzypczyk.revisions.presentation.config.RevisionsConfigState
import com.rafalskrzypczyk.revisions.presentation.config.RevisionsConfigUIEvents
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * E2E-REV-06: stan pusty w konfiguracji powtórek.
 *
 * Regresja MQ-20-B — filtr zawężający pulę do zera pytań zamieniał dialog w ślepą uliczkę:
 * `EmptyStateCard` renderował się *zamiast* chipów kryterium, więc jedynego wyboru, który
 * doprowadził do pustej puli, nie dało się już cofnąć. Test pilnuje rozdziału dwóch stanów:
 * pusta pula zostawia kontrolki, niedostępny tryb (< 10 odpowiedzi) nadal ich nie pokazuje.
 */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class, sdk = [34])
class RevisionsEmptyStateHarnessTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    private val category = RevisionCategory(
        id = 1L,
        title = "Kardiologia",
        mode = QuizMode.MainMode,
        totalQuestionsCount = 50,
        answeredQuestionsCount = 12,
        isEligible = true
    )

    /** Konfiguracja po zawężeniu kryterium do „poniżej 50%", które nie zwróciło żadnego pytania. */
    private val emptyPoolState = RevisionsConfigState(
        selectedMode = QuizMode.MainMode,
        categoriesList = listOf(category),
        selectedCategory = category,
        selectedCriterion = RevisionCriterion.UNDER_50,
        selectedLimit = null,
        availableQuestionsCount = 0,
        availableLimits = listOf(null),
        responseState = ResponseState.Success,
        isModeEligible = true,
        isEmptyState = true,
        isConfigDialogVisible = true
    )

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun `empty pool keeps criterion chips reachable so the filter can be undone`() {
        val events = mutableListOf<RevisionsConfigUIEvents>()
        setContent(emptyPoolState) { events += it }

        composeRule.onNodeWithTag(TestTags.REVISIONS_EMPTY_STATE).assertExists()
        // Sesji nie da się rozpocząć — ale to nie może oznaczać braku drogi powrotnej.
        composeRule.onNodeWithTag(TestTags.REVISIONS_START_BUTTON).assertIsNotEnabled()

        // performSemanticsAction zamiast performClick — pod Robolectrikiem wstrzykiwanie dotyku
        // nie trafia w okno dialogu, a sprawdzamy tu obecność i działanie akcji chipa, nie hit-testing.
        composeRule.onNodeWithTag(TestTags.revisionsCriterionChip(RevisionCriterion.WORST.name))
            .performSemanticsAction(SemanticsActions.OnClick)

        assertEquals(
            "Klik w chip kryterium musi wyjść ze stanu pustego",
            listOf(RevisionsConfigUIEvents.SelectCriterion(RevisionCriterion.WORST)),
            events
        )
    }

    @Test
    fun `unavailable mode still hides the configuration controls`() {
        setContent(emptyPoolState.copy(isModeEligible = false, isEmptyState = false)) {}

        composeRule.onNodeWithTag(TestTags.REVISIONS_EMPTY_STATE).assertExists()
        // Tryb bez 10 odpowiedzi nie ma czego konfigurować — wyjście przez Anuluj.
        composeRule.onNodeWithTag(TestTags.revisionsCriterionChip(RevisionCriterion.WORST.name))
            .assertDoesNotExist()
        composeRule.onNodeWithTag(TestTags.REVISIONS_START_BUTTON).assertIsNotEnabled()
    }

    private fun setContent(
        state: RevisionsConfigState,
        onEvent: (RevisionsConfigUIEvents) -> Unit
    ) {
        composeRule.setContent {
            ParamedQuizTheme {
                RevisionsConfigScreen(
                    state = state,
                    onEvent = onEvent,
                    onNavigateBack = {},
                    onStartSession = { _, _, _, _ -> }
                )
            }
        }
    }
}
