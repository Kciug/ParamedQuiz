package com.rafalskrzypczyk.core.analytics

import com.rafalskrzypczyk.core.analytics.AnalyticsParams as P

/**
 * Kontrakt pomiarowy aplikacji — pełna lista zdarzeń wraz z parametrami.
 *
 * **Wspólny z iOS.** Nazwy i parametry muszą się zgadzać na obu platformach; zmiana tutaj jest
 * zmianą kontraktu dla obu. Odpowiednik po tamtej stronie:
 * `MediQuiz/Modules/Analytics/Domain/Entities/AnalyticsEvent.swift`.
 *
 * Nazwa zdarzenia wysłana raz na produkcję jest niezmienna: GA4 nie robi backfillu, a „Modify
 * event" nie działa wstecz, więc rename po wydaniu daje dwa trwale rozłączne szeregi.
 *
 * Konwencja nazw jest konwencją Google, nie naszą: `obiekt_czasownik` w formie podstawowej
 * (`quiz_start`, `tutorial_complete`, `select_item`), nigdy imiesłów. Do 40 znaków, `snake_case`,
 * bez prefiksów `firebase_`, `google_`, `ga_` i bez nazw zarezerwowanych — stąd `notification_tap`
 * zamiast zajętego przez FCM `notification_open`.
 *
 * Typy wartości: wyłącznie `String`, `Long` lub `Double`. Wartości logiczne jako `Long` 1/0 (patrz
 * [asParam]), teksty do 100 znaków. Parametry opcjonalne są **pomijane**, gdy nieznane — nigdy
 * wysyłane jako pusty string.
 */
sealed interface AnalyticsEvent {
    val name: String
    val params: Map<String, Any> get() = emptyMap()

    // region Monetyzacja

    /** Pokazanie oferty: ekran sklepu, panel trybu na Home, okno kategorii, ściana wersji próbnej. */
    data class PaywallViewed(
        val paywall: Paywall,
        val productId: String,
        val hasPrice: Boolean,
        val mode: String? = null,
        val categoryId: Long? = null,
    ) : AnalyticsEvent {
        override val name = "paywall_view"
        override val params = buildMap<String, Any> {
            put(P.PAYWALL, paywall.value)
            put(P.PRODUCT_ID, productId)
            put(P.HAS_PRICE, hasPrice.asParam())
            mode?.let { put(P.MODE, it) }
            categoryId?.let { put(P.CATEGORY_ID, it) }
        }
    }

    /** Klik w przycisk zakupu, tuż przed `launchBillingFlow`. Emituje wyłącznie PurchaseFunnelTracker. */
    data class PurchaseStarted(
        val paywall: Paywall,
        val productId: String,
        val productType: ProductType,
        val priceMicros: Long,
        val currency: String,
    ) : AnalyticsEvent {
        override val name = "purchase_start"
        override val params = mapOf(
            P.PAYWALL to paywall.value,
            P.PRODUCT_ID to productId,
            P.PRODUCT_TYPE to productType.value,
            P.PRICE_MICROS to priceMicros,
            P.CURRENCY to currency,
        )
    }

    /**
     * Zakup potwierdzony przez Google Play.
     *
     * Bez `value` i `currency`: przychód raportuje automatyczne `in_app_purchase`, a GA4 **nie
     * deduplikuje** go z ręcznie wysłanym `purchase` na strumieniach aplikacyjnych — wysyłanie obu
     * podwaja przychód. To zdarzenie jest znacznikiem lejka, nie źródłem przychodu.
     */
    data class PurchaseCompleted(
        val paywall: Paywall,
        val productId: String,
        val productType: ProductType,
        val mode: String? = null,
        val categoryId: Long? = null,
    ) : AnalyticsEvent {
        override val name = "purchase_complete"
        override val params = buildMap<String, Any> {
            put(P.PAYWALL, paywall.value)
            put(P.PRODUCT_ID, productId)
            put(P.PRODUCT_TYPE, productType.value)
            mode?.let { put(P.MODE, it) }
            categoryId?.let { put(P.CATEGORY_ID, it) }
        }
    }

    data class PurchasePending(
        val paywall: Paywall,
        val productId: String,
    ) : AnalyticsEvent {
        override val name = "purchase_pending"
        override val params = mapOf(P.PAYWALL to paywall.value, P.PRODUCT_ID to productId)
    }

    data class PurchaseCancelled(
        val paywall: Paywall,
        val productId: String,
    ) : AnalyticsEvent {
        override val name = "purchase_cancel"
        override val params = mapOf(P.PAYWALL to paywall.value, P.PRODUCT_ID to productId)
    }

    data class PurchaseFailed(
        val paywall: Paywall,
        val productId: String,
        val errorCode: String,
    ) : AnalyticsEvent {
        override val name = "purchase_fail"
        override val params = mapOf(
            P.PAYWALL to paywall.value,
            P.PRODUCT_ID to productId,
            P.ERROR_CODE to errorCode,
        )
    }

    /**
     * Próba zakupu bez `ProductDetails` w cache. Łapie cichą utratę przychodu: użytkownik klika
     * „Kup" i nie dzieje się nic, bo `launchBillingFlow` po cichu wychodzi.
     */
    data class PaywallPriceMissing(
        val paywall: Paywall,
        val productId: String,
    ) : AnalyticsEvent {
        override val name = "paywall_price_missing"
        override val params = mapOf(P.PAYWALL to paywall.value, P.PRODUCT_ID to productId)
    }

    data class TrialStarted(val mode: String) : AnalyticsEvent {
        override val name = "trial_start"
        override val params = mapOf(P.MODE to mode)
    }

    /** Wyczerpanie puli darmowych pytań. */
    data class TrialWallReached(
        val mode: String,
        val answeredCount: Int,
    ) : AnalyticsEvent {
        override val name = "trial_wall_reach"
        override val params = mapOf(P.MODE to mode, P.ANSWERED_COUNT to answeredCount.toLong())
    }

    // endregion

    // region Użycie

    /**
     * [screenClass] jest z definicji rowne [screenName] — tak ustala kontrakt iOS. Osobny parametr
     * zostaje, bo GA4 ma dla niego wbudowany wymiar i wypelnia go automatycznie nazwa Activity,
     * ktora u nas jest zawsze ta sama.
     *
     * [mode] jest ustawiany dla korzeni trybow: iOS rozroznia „quiz w trybie glownym" od „quiz
     * w Swipe" wlasnie tym parametrem, nie osobna nazwa ekranu.
     */
    data class ScreenView(
        val screenName: String,
        val mode: String? = null,
        val screenClass: String = screenName,
    ) : AnalyticsEvent {
        override val name = "screen_view"
        override val params = buildMap<String, Any> {
            put(P.SCREEN_NAME, screenName)
            put(P.SCREEN_CLASS, screenClass)
            mode?.let { put(P.MODE, it) }
        }
    }

    /** [locked] mierzy popyt na treść jeszcze niekupioną — inna diagnoza niż sam wolumen sprzedaży. */
    data class ModeSelected(
        val mode: String,
        val locked: Boolean,
    ) : AnalyticsEvent {
        override val name = "mode_select"
        override val params = mapOf(P.MODE to mode, P.LOCKED to locked.asParam())
    }

    data class AddonTapped(
        val addon: HomeAddon,
        val available: Boolean,
    ) : AnalyticsEvent {
        override val name = "addon_tap"
        override val params = mapOf(P.ADDON to addon.value, P.AVAILABLE to available.asParam())
    }

    data class CategorySelected(
        val mode: String,
        val categoryId: Long,
        val locked: Boolean,
    ) : AnalyticsEvent {
        override val name = "category_select"
        override val params = mapOf(
            P.MODE to mode,
            P.CATEGORY_ID to categoryId,
            P.LOCKED to locked.asParam(),
        )
    }

    data class QuizStarted(
        val mode: String,
        val quizType: QuizType,
        val questionCount: Int,
        val isFreePreview: Boolean,
        val categoryId: Long? = null,
        val categoryName: String? = null,
    ) : AnalyticsEvent {
        override val name = "quiz_start"
        override val params = buildMap<String, Any> {
            put(P.MODE, mode)
            put(P.QUIZ_TYPE, quizType.value)
            put(P.QUESTION_COUNT, questionCount.toLong())
            put(P.IS_FREE_PREVIEW, isFreePreview.asParam())
            categoryId?.let { put(P.CATEGORY_ID, it) }
            categoryName?.let { put(P.CATEGORY_NAME, it.take(MAX_PARAM_LENGTH)) }
        }
    }

    /**
     * Każda zatwierdzona odpowiedź, łącznie z ponownymi podejściami w powtórkach.
     *
     * Najliczniejsze zdarzenie kontraktu: w trybie Swipe i Tłumaczeń jedna sesja to cała pula
     * pytań z Firestore, więc setki emisji. `mode` i `quiz_type` muszą być wyliczone dokładnie
     * tak, jak w [QuizStarted] tej samej sesji — inaczej odpowiedzi nie dołożą się do sesji.
     */
    data class QuestionAnswered(
        val mode: String,
        val quizType: QuizType,
        val isCorrect: Boolean,
        val categoryId: Long? = null,
    ) : AnalyticsEvent {
        override val name = "question_answered"
        override val params = buildMap<String, Any> {
            put(P.MODE, mode)
            put(P.QUIZ_TYPE, quizType.value)
            put(P.IS_CORRECT, isCorrect.asParam())
            categoryId?.let { put(P.CATEGORY_ID, it) }
        }
    }

    /**
     * [durationSec] jest dodatkiem Androida wobec kontraktu iOS — mierzone od inicjalizacji sesji
     * do jej logicznego końca, bez czasu oglądania reklamy.
     *
     * [maxStreak] to najdłuższa seria poprawnych odpowiedzi **pod rząd** w tej sesji (nie seria
     * dni). Liczona od zera w każdym trybie; w Swipe osobno od `bestStreak`, który startuje
     * od rekordu wszech czasów.
     */
    data class QuizCompleted(
        val mode: String,
        val quizType: QuizType,
        val questionCount: Int,
        val answeredCount: Int,
        val correctCount: Int,
        val isEarlyExit: Boolean,
        val isFreePreview: Boolean,
        val durationSec: Long,
        val maxStreak: Int,
        val categoryId: Long? = null,
    ) : AnalyticsEvent {
        override val name = "quiz_complete"
        override val params = buildMap<String, Any> {
            put(P.MODE, mode)
            put(P.QUIZ_TYPE, quizType.value)
            put(P.QUESTION_COUNT, questionCount.toLong())
            put(P.ANSWERED_COUNT, answeredCount.toLong())
            put(P.CORRECT_COUNT, correctCount.toLong())
            put(P.INCORRECT_COUNT, (answeredCount - correctCount).coerceAtLeast(0).toLong())
            put(P.IS_EARLY_EXIT, isEarlyExit.asParam())
            put(P.IS_FREE_PREVIEW, isFreePreview.asParam())
            put(P.DURATION_SEC, durationSec)
            put(P.MAX_STREAK, maxStreak.toLong())
            categoryId?.let { put(P.CATEGORY_ID, it) }
        }
    }

    data class RevisionConfigured(
        val criterion: String,
        val mode: String,
        val categoryCount: Int,
        val questionCount: Int,
    ) : AnalyticsEvent {
        override val name = "revision_config"
        override val params = mapOf(
            P.CRITERION to criterion,
            P.MODE to mode,
            P.CATEGORY_COUNT to categoryCount.toLong(),
            P.QUESTION_COUNT to questionCount.toLong(),
        )
    }

    data class OnboardingCompleted(
        val skipped: Boolean,
        val lastPage: Int,
    ) : AnalyticsEvent {
        override val name = "onboarding_complete"
        override val params = mapOf(P.SKIPPED to skipped.asParam(), P.LAST_PAGE to lastPage.toLong())
    }

    data class ModeOnboardingCompleted(val mode: String) : AnalyticsEvent {
        override val name = "mode_onboarding_complete"
        override val params = mapOf(P.MODE to mode)
    }

    // endregion

    // region Retencja

    /**
     * Zadanie dnia zaliczone. Nie jest to duplikat `quiz_complete` z `quiz_type = daily_quest`:
     * tamto zapada także przy wyjściu przed pierwszą odpowiedzią, kiedy dzień nie jest zużyty.
     */
    data class DailyQuestCompleted(val streakCount: Int) : AnalyticsEvent {
        override val name = "daily_quest_complete"
        override val params = mapOf(P.STREAK_COUNT to streakCount.toLong())
    }

    /**
     * Podbicie serii dziennej, co najwyżej raz na dobę. [streakCount] to seria **po** podbiciu;
     * po przerwaniu serii licznik startuje od 1, więc restart widać jako `streak_count = 1`.
     *
     * Nie mylić z `max_streak` w [QuizCompleted] — tam chodzi o najdłuższą serię poprawnych
     * odpowiedzi w jednej sesji, tu o serię dni.
     */
    data class StreakIncremented(val streakCount: Int) : AnalyticsEvent {
        override val name = "streak_increment"
        override val params = mapOf(P.STREAK_COUNT to streakCount.toLong())
    }

    data object RatingPromptViewed : AnalyticsEvent {
        override val name = "rating_prompt_view"
    }

    data class RatingPromptAnswered(
        val rating: Int,
        val action: RatingAction,
    ) : AnalyticsEvent {
        override val name = "rating_prompt_answer"
        override val params = mapOf(P.RATING to rating.toLong(), P.ACTION to action.value)
    }

    data object NotificationPromptViewed : AnalyticsEvent {
        override val name = "notification_prompt_view"
    }

    data class NotificationPromptAnswered(val action: NotificationPromptAction) : AnalyticsEvent {
        override val name = "notification_prompt_answer"
        override val params = mapOf(P.ACTION to action.value)
    }

    /**
     * Odpowiedź na **systemowy** dialog `POST_NOTIFICATIONS` — nie mylić z [NotificationPromptAnswered],
     * który dotyczy naszego własnego pytania poprzedzającego dialog.
     *
     * Logowane tylko raz na instalację. Android pokazuje dialog najwyżej dwa razy, a po trwałej
     * odmowie callback wraca natychmiast z `false` — bez bramki każde tapnięcie przełącznika
     * w ustawieniach produkowałoby fałszywe `granted = 0`.
     */
    data class NotificationPermissionAnswered(val granted: Boolean) : AnalyticsEvent {
        override val name = "notification_permission"
        override val params = mapOf(P.GRANTED to granted.asParam())
    }

    /** Nazwa z „-tap": `notification_open` jest zarezerwowane przez FCM i zbierane automatycznie. */
    data class NotificationTapped(
        val destination: String,
        val isRemote: Boolean,
    ) : AnalyticsEvent {
        override val name = "notification_tap"
        override val params = mapOf(P.DESTINATION to destination, P.IS_REMOTE to isRemote.asParam())
    }

    data class NewsBannerDismissed(val bannerId: String) : AnalyticsEvent {
        override val name = "news_banner_dismiss"
        override val params = mapOf(P.BANNER_ID to bannerId)
    }

    /** Zdarzenie rekomendowane przez Google — zasila gotowe raporty i predefiniowane wymiary. */
    data class SignUp(val method: AuthMethod) : AnalyticsEvent {
        override val name = "sign_up"
        override val params = mapOf(P.METHOD to method.value)
    }

    /** Zdarzenie rekomendowane przez Google. Logowanie do istniejącego konta, nie rejestracja. */
    data class Login(val method: AuthMethod) : AnalyticsEvent {
        override val name = "login"
        override val params = mapOf(P.METHOD to method.value)
    }

    data object SignOut : AnalyticsEvent {
        override val name = "sign_out"
    }

    /** Konto usunięte na życzenie użytkownika. Emitowane tylko ze ścieżki zakończonej sukcesem. */
    data object AccountDeleted : AnalyticsEvent {
        override val name = "account_delete"
    }

    data class IssueReported(val mode: String) : AnalyticsEvent {
        override val name = "issue_report"
        override val params = mapOf(P.MODE to mode)
    }

    // endregion

    // region Reklamy

    /**
     * Interstitial faktycznie wyświetlony (nie „zlecony do wyświetlenia").
     *
     * Nie dubluje automatycznego `ad_impression` z AdMob: tamto niesie przychód, to odpowiada na
     * pytanie, ile reklam widzi użytkownik i po ilu odpowiedziach — czyli czy częstotliwość
     * z Remote Config nie jest za agresywna.
     */
    data class AdShown(
        val adUnit: AdUnit,
        val answersSinceLastAd: Int,
    ) : AnalyticsEvent {
        override val name = "ad_shown"
        override val params = mapOf(
            P.AD_FORMAT to AD_FORMAT_INTERSTITIAL,
            P.AD_UNIT to adUnit.value,
            P.ANSWERS_SINCE_LAST_AD to answersSinceLastAd.toLong(),
        )
    }

    /**
     * Nieudane załadowanie albo wyświetlenie reklamy pełnoekranowej.
     *
     * [errorCode] jest **tekstem**, mimo że AdMob zwraca liczbę i mimo że kontrakt iOS mówi o `Int`:
     * `error_code` jest już zarejestrowane jako wymiar tekstowy dla `purchase_fail`, a GA4 pozwala
     * zarejestrować nazwę parametru tylko raz — jako wymiar **albo** metrykę. Wysłanie liczby pod tą
     * samą nazwą zepsułoby istniejący wymiar. Do uzgodnienia z iOS.
     */
    data class AdLoadFailed(
        val stage: AdStage,
        val errorCode: String,
        val adUnit: AdUnit,
    ) : AnalyticsEvent {
        override val name = "ad_load_failed"
        override val params = mapOf(
            P.STAGE to stage.value,
            P.ERROR_CODE to errorCode,
            P.AD_UNIT to adUnit.value,
        )
    }

    // endregion

    // region Zdrowie

    /** Emitowane z implementacji ErrorLogger — jedno miejsce pokrywa całą aplikację. */
    data class AppErrorOccurred(
        val origin: String,
        val errorType: String,
    ) : AnalyticsEvent {
        override val name = "app_error"
        override val params = mapOf(P.ORIGIN to origin, P.ERROR_TYPE to errorType)
    }

    // endregion

    companion object {
        /** Limit GA4 na wartość parametru. Dłuższe są odrzucane, nie przycinane. */
        const val MAX_PARAM_LENGTH = 100
    }
}
