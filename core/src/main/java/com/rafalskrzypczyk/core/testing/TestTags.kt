package com.rafalskrzypczyk.core.testing

/**
 * Stałe test-tagów dla testów UI/E2E (Compose). Trzymane w produkcyjnym kodzie, bo `Modifier.testTag`
 * jest nakładany w composable'ach. Selektory po tagach są stabilniejsze niż po tekście (który zmienia
 * się między językami/wersjami treści).
 */
object TestTags {
    // Regulamin (Terms of Service)
    const val TOS_ACCEPT_BUTTON = "tos_accept_button"

    // Ekran główny (Home)
    const val HOME_ROOT = "home_root"

    // Quiz (wspólne dla trybów opartych o QuizGameContent)
    const val QUIZ_SUBMIT_BUTTON = "quiz_submit_button"
    const val QUIZ_NEXT_BUTTON = "quiz_next_button"
    const val QUIZ_FINISHED_ROOT = "quiz_finished_root"

    // Tryb Swipe
    const val SWIPE_TRIAL_BUY_BUTTON = "swipe_trial_buy_button"

    // Tryb Tłumaczenia
    const val TRANSLATION_TRIAL_BUY_BUTTON = "translation_trial_buy_button"

    // Dialog zakupu (wspólny — kategorie, tryby)
    const val PURCHASE_DIALOG_BUY_BUTTON = "purchase_dialog_buy_button"
}
