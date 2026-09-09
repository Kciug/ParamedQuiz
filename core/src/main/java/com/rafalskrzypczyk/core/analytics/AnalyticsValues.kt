package com.rafalskrzypczyk.core.analytics

import com.rafalskrzypczyk.core.utils.QuizMode

/** Nazwy parametrów. Każdy z nich wymaga ręcznej rejestracji jako custom dimension/metric w konsoli. */
object AnalyticsParams {
    const val SURFACE = "surface"
    const val PRODUCT_ID = "product_id"
    const val PRICE_MICROS = "price_micros"
    const val CURRENCY = "currency"
    const val VALUE = "value"
    const val HAS_PRICE = "has_price"
    const val ERROR_CODE = "error_code"
    const val ERROR_TYPE = "error_type"
    const val ORIGIN = "origin"
    const val MODE = "mode"
    const val SOURCE = "source"
    const val COMPLETION = "completion"
    const val QUESTIONS_COUNT = "questions_count"
    const val QUESTIONS_ANSWERED = "questions_answered"
    const val CORRECT_ANSWERS = "correct_answers"
    const val DURATION_SEC = "duration_sec"
    const val IS_TRIAL = "is_trial"
    const val LOCKED = "locked"
    const val AVAILABLE = "available"
    const val ADDON = "addon"
    const val CATEGORY_ID = "category_id"
    const val CRITERION = "criterion"
    const val CATEGORIES_COUNT = "categories_count"
    const val SKIPPED = "skipped"
    const val LAST_PAGE = "last_page"
    const val ACTION = "action"
    const val RATING = "rating"
    const val DESTINATION = "destination"
    const val BANNER_ID = "banner_id"
    const val METHOD = "method"
    const val SCREEN_NAME = "screen_name"
    const val SCREEN_CLASS = "screen_class"
}

/** Miejsce sprzedaży. `UNKNOWN` to wynik zakupu bez poprzedzającego startu (np. kod promocyjny). */
enum class PurchaseSurface(val value: String) {
    STORE("store"),
    HOME_SHEET("home_sheet"),
    CATEGORY_SHEET("category_sheet"),
    TRIAL_END("trial_end"),
    UNKNOWN("unknown"),
}

/** Skąd użytkownik wszedł w sesję quizu. */
enum class QuizSource(val value: String) {
    HOME("home"),
    CATEGORY("category"),
    DAILY_EXERCISE("daily_exercise"),
    REVISIONS("revisions"),
}

/** Rozróżnia sesję dograną do końca od porzuconej. */
enum class QuizCompletion(val value: String) {
    COMPLETED("completed"),
    EARLY_EXIT("early_exit"),
}

/** Dodatki z ekranu głównego. */
enum class HomeAddon(val value: String) {
    DAILY("daily"),
    REVISIONS("revisions"),
    STORE("store"),
}

/** Wybór w karcie oceny aplikacji. */
enum class RatingAction(val value: String) {
    STORE("store"),
    FEEDBACK("feedback"),
    DISMISS("dismiss"),
    NEVER_AGAIN("never_again"),
}

/** Odpowiedź na prompt zgody na powiadomienia. */
enum class NotificationPromptAction(val value: String) {
    ACCEPTED("accepted"),
    DENIED("denied"),
    DISMISSED("dismissed"),
}

/** Metoda uwierzytelnienia w evencie `signup_completed`. */
enum class AuthMethod(val value: String) {
    PASSWORD("password"),
    GOOGLE("google"),
}

/**
 * Stałe [QuizMode] są PascalCase, więc `name.lowercase()` dałoby `mainmode`.
 * Wyczerpujący `when` bez `else` — nowy tryb zepsuje kompilację, nie dane.
 */
fun QuizMode.analyticsName(): String = when (this) {
    QuizMode.MainMode -> "main"
    QuizMode.SwipeMode -> "swipe"
    QuizMode.TranslationMode -> "translation"
    QuizMode.CemMode -> "cem"
    QuizMode.RevisionsMode -> "revisions"
}

/** GA4 nie ma typu logicznego w parametrach — wysyłamy `true`/`false` jako tekst. */
internal fun Boolean.asParam(): String = toString()
