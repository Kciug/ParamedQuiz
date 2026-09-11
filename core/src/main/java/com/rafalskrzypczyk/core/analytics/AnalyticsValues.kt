package com.rafalskrzypczyk.core.analytics

import com.rafalskrzypczyk.core.utils.QuizMode

/**
 * Nazwy parametrów. Wspólne z iOS — patrz `docs/20-analityka-i-pomiar.md`.
 *
 * Każdy parametr, który ma być widoczny w raportach, wymaga ręcznej rejestracji jako custom
 * dimension albo metric w konsoli Firebase.
 */
object AnalyticsParams {
    const val PAYWALL = "paywall"
    const val PRODUCT_ID = "product_id"
    const val PRODUCT_TYPE = "product_type"
    const val PRICE_MICROS = "price_micros"
    const val CURRENCY = "currency"
    const val HAS_PRICE = "has_price"
    const val ERROR_CODE = "error_code"
    const val ERROR_TYPE = "error_type"
    const val ORIGIN = "origin"
    const val MODE = "mode"
    const val QUIZ_TYPE = "quiz_type"
    const val QUESTION_COUNT = "question_count"
    const val ANSWERED_COUNT = "answered_count"
    const val CORRECT_COUNT = "correct_count"
    const val INCORRECT_COUNT = "incorrect_count"
    const val MAX_STREAK = "max_streak"
    const val IS_EARLY_EXIT = "is_early_exit"
    const val IS_FREE_PREVIEW = "is_free_preview"
    const val DURATION_SEC = "duration_sec"
    const val CATEGORY_ID = "category_id"
    const val CATEGORY_NAME = "category_name"
    const val CATEGORY_COUNT = "category_count"
    const val LOCKED = "locked"
    const val AVAILABLE = "available"
    const val ADDON = "addon"
    const val CRITERION = "criterion"
    const val SKIPPED = "skipped"
    const val LAST_PAGE = "last_page"
    const val ACTION = "action"
    const val RATING = "rating"
    const val DESTINATION = "destination"
    const val GRANTED = "granted"
    const val STREAK_COUNT = "streak_count"
    const val AD_FORMAT = "ad_format"
    const val AD_UNIT = "ad_unit"
    const val ANSWERS_SINCE_LAST_AD = "answers_since_last_ad"
    const val STAGE = "stage"
    const val IS_REMOTE = "is_remote"
    const val BANNER_ID = "banner_id"
    const val METHOD = "method"
    const val SCREEN_NAME = "screen_name"
    const val SCREEN_CLASS = "screen_class"
}

/**
 * Miejsce, w którym pokazała się oferta.
 *
 * [TRIAL_END] i [UNKNOWN] są dodatkiem Androida wobec kontraktu iOS: pierwsze rozdziela ścianę
 * wersji próbnej od zwykłego panelu trybu (bez tego nie da się policzyć konwersji triala), drugie
 * to kubełek na wynik zakupu bez poprzedzającego startu (np. kod promocyjny z Google Play).
 */
enum class Paywall(val value: String) {
    STORE("store"),
    CATEGORY("category"),
    MODE("mode"),
    AD_FREE("ad_free"),
    TRIAL_END("trial_end"),
    UNKNOWN("unknown"),
}

/**
 * Nazwy ekranow zagniezdzonych `NavHost`ow trybow, wspolne z iOS. Nazwy ekranow glownego
 * `NavHost`a mapuje `ScreenNames` w module `:app` — te tutaj sa potrzebne poza nim, bo
 * `quiz_end` raportuja ViewModele (ekran wyniku to stan, nie trasa).
 */
object ScreenName {
    const val CATEGORIES = "categories"
    const val QUIZ = "quiz"
    const val QUIZ_END = "quiz_end"
    const val REVISION_SETUP = "revision_setup"
}

/** Jedyny format reklamy, jaki emitujemy — pozostaje parametrem, bo tak chce kontrakt iOS. */
const val AD_FORMAT_INTERSTITIAL = "interstitial"

/** Etap, na ktorym reklama pelnoekranowa przepadla. */
enum class AdStage(val value: String) {
    LOAD("load"),
    PRESENT("present"),
}

/**
 * Jednostka reklamowa, z ktorej pochodzi odslona. Bez tego podzialu ruch z buildow debug
 * i staging (jednostki testowe Google) mieszalby sie z produkcyjnym.
 */
enum class AdUnit(val value: String) {
    TEST("test"),
    PRODUCTION("production"),
}

/** Rodzaj kupowanego produktu. */
enum class ProductType(val value: String) {
    CATEGORY("category"),
    MODE("mode"),
    AD_FREE("ad_free"),
    PREMIUM("premium"),
}

/**
 * Rodzaj sesji quizu. Powtórki są tu, a nie w [AnalyticsParams.MODE] — `mode` niesie tryb treści,
 * którą się powtarza.
 */
enum class QuizType(val value: String) {
    CATEGORY("category"),
    DAILY_QUEST("daily_quest"),
    REVISION("revision"),

    /** Cała pula trybu Swipe albo Tłumaczeń. */
    FULL("full"),

    /** Darmowy fragment płatnego trybu. */
    FREE_PREVIEW("free_preview"),
}

/** Dodatki z ekranu głównego. Zdarzenie własne Androida. */
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

/** Odpowiedź na prompt zgody na powiadomienia (wewnętrzny, nie systemowy). */
enum class NotificationPromptAction(val value: String) {
    ACCEPTED("accepted"),
    DENIED("denied"),
    DISMISSED("dismissed"),
}

/** Metoda uwierzytelnienia w `sign_up` i `login`. */
enum class AuthMethod(val value: String) {
    EMAIL("email"),
    GOOGLE("google"),
}

/**
 * Wartości słownika `mode`, wspólne z iOS: `main` | `cem` | `swipe` | `translations`.
 *
 * Uwaga na liczbę mnogą w `translations` — zgadza się z SKU (`mediquiz_translations_mode`), a nie
 * z nazwą modułu. [QuizMode.RevisionsMode] nie ma odpowiednika w słowniku, bo powtórki opisuje
 * [QuizType.REVISION]; mapowanie poniżej jest wyłącznie zabezpieczeniem wyczerpalności `when`.
 */
fun QuizMode.analyticsName(): String = when (this) {
    QuizMode.MainMode -> "main"
    QuizMode.SwipeMode -> "swipe"
    QuizMode.TranslationMode -> "translations"
    QuizMode.CemMode -> "cem"
    QuizMode.RevisionsMode -> "revisions"
}

/**
 * GA4 nie ma typu logicznego w parametrach, a Android nie przyjmuje `Boolean` w Bundle zdarzenia.
 * Kontrakt cross-platform ustala `Int` 1/0 — nie tekst „true"/„false".
 */
internal fun Boolean.asParam(): Long = if (this) 1L else 0L

/** SKU → rodzaj produktu. Kategorie mają dynamiczne identyfikatory, więc są przypadkiem domyślnym. */
fun productTypeOf(productId: String, fullPackageId: String, adFreeId: String, modeIds: Set<String>): ProductType =
    when {
        productId == fullPackageId -> ProductType.PREMIUM
        productId == adFreeId -> ProductType.AD_FREE
        productId in modeIds -> ProductType.MODE
        else -> ProductType.CATEGORY
    }
