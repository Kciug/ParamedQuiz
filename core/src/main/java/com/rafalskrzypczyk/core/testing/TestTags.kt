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
}
