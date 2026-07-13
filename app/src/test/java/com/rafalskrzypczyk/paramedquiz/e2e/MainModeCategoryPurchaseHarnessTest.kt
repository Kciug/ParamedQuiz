package com.rafalskrzypczyk.paramedquiz.e2e

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.ViewModelStore
import com.rafalskrzypczyk.billing.domain.BillingRepository
import com.rafalskrzypczyk.core.billing.PremiumStatusProvider
import com.rafalskrzypczyk.core.testing.TestTags
import com.rafalskrzypczyk.core.ui.theme.ParamedQuizTheme
import com.rafalskrzypczyk.firestore.domain.models.CategoryDTO
import com.rafalskrzypczyk.main_mode.domain.quiz_categories.MMCategoriesUseCases
import com.rafalskrzypczyk.main_mode.presentation.categories_screen.MMCategoriesScreen
import com.rafalskrzypczyk.main_mode.presentation.categories_screen.MMCategoriesVM
import com.rafalskrzypczyk.paramedquiz.e2e.fakes.FakeFirestoreApi
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import javax.inject.Inject

/**
 * E2E-MAIN-02: klik zablokowanej (płatnej) kategorii otwiera okno zakupu.
 *
 * Kategoria z `free=false` przy braku premium ⇒ locked → klik karty otwiera `PurchaseCategoryDialog`.
 */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class, sdk = [34])
class MainModeCategoryPurchaseHarnessTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @Inject
    lateinit var fakeFirestore: FakeFirestoreApi

    @Inject
    lateinit var useCases: MMCategoriesUseCases

    @Inject
    lateinit var billingRepository: BillingRepository

    @Inject
    lateinit var premiumStatusProvider: PremiumStatusProvider

    private val paidCategoryId = 200L
    private val viewModelStore = ViewModelStore()

    @Before
    fun setUp() {
        hiltRule.inject()
        fakeFirestore.quizCategories = listOf(
            CategoryDTO(
                id = paidCategoryId,
                title = "KATEGORIA_PLATNA",
                subtitle = "Opis kategorii",
                questionsCount = 5,
                free = false
            )
        )
    }

    @Test
    fun `clicking locked category opens purchase dialog`() {
        val viewModel = MMCategoriesVM(useCases, billingRepository, premiumStatusProvider)
            .also { viewModelStore.put("vm", it) }

        composeRule.setContent {
            val state by viewModel.state.collectAsState()
            ParamedQuizTheme {
                MMCategoriesScreen(
                    state = state,
                    onEvent = viewModel::onEvent,
                    onNavigateBack = {},
                    onUserPanel = {},
                    onStartCategory = { _, _ -> }
                )
            }
        }

        // Zablokowana kategoria widoczna → klik.
        waitForText("KATEGORIA_PLATNA")
        composeRule.onNodeWithText("KATEGORIA_PLATNA").performClick()

        // Otwiera się dialog zakupu dla właśnie tej kategorii.
        composeRule.waitUntil(timeoutMillis = 15_000) {
            viewModel.state.value.selectedCategoryForPurchase != null
        }
        assertEquals(paidCategoryId, viewModel.state.value.selectedCategoryForPurchase?.id)

        waitForTag(TestTags.PURCHASE_DIALOG_BUY_BUTTON)
        composeRule.onNodeWithTag(TestTags.PURCHASE_DIALOG_BUY_BUTTON).assertExists()
    }

    @After
    fun tearDown() {
        viewModelStore.clear()
    }

    private fun waitForText(text: String) {
        composeRule.waitUntil(timeoutMillis = 15_000) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForTag(tag: String) {
        composeRule.waitUntil(timeoutMillis = 15_000) {
            composeRule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
