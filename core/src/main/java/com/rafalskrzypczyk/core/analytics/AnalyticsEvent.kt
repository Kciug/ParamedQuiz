package com.rafalskrzypczyk.core.analytics

import com.rafalskrzypczyk.core.analytics.AnalyticsParams as P

/**
 * Kontrakt pomiarowy aplikacji — pełna lista zdarzeń wraz z parametrami.
 *
 * Nazwa zdarzenia wysłana raz na produkcję jest niezmienna: użytkownicy nie aktualizują aplikacji
 * natychmiast, więc zmiana nazwy rozbiłaby raport na dwa rozłączne szeregi. Kontrakt jest
 * append-only — dodajemy nowe warianty, nie zmieniamy i nie usuwamy istniejących.
 *
 * Zasady nazewnictwa: snake_case, angielski, do 40 znaków, wzorzec `obiekt_czynność` w czasie
 * przeszłym. Zabronione prefiksy `firebase_`, `google_`, `ga_`. Nie używamy nazw zarezerwowanych —
 * stąd `notification_opened` zamiast zajętego przez FCM `notification_open`.
 *
 * Wartości parametrów mogą być wyłącznie typu String, Long lub Double (ograniczenie Bundle w GA4).
 * Wartości logiczne wysyłamy jako tekst — patrz [asParam].
 */
sealed interface AnalyticsEvent {
    val name: String
    val params: Map<String, Any> get() = emptyMap()

    // region Monetyzacja

    /** Pokazanie oferty: ekran sklepu, panel na Home, okno kategorii, panel końca triala. */
    data class PaywallShown(
        val surface: PurchaseSurface,
        val productId: String,
        val hasPrice: Boolean,
    ) : AnalyticsEvent {
        override val name = "paywall_shown"
        override val params = mapOf(
            P.SURFACE to surface.value,
            P.PRODUCT_ID to productId,
            P.HAS_PRICE to hasPrice.asParam(),
        )
    }

    /** Klik w przycisk zakupu, tuż przed launchBillingFlow. Emituje wyłącznie PurchaseFunnelTracker. */
    data class PurchaseStarted(
        val surface: PurchaseSurface,
        val productId: String,
        val priceMicros: Long,
        val currency: String,
    ) : AnalyticsEvent {
        override val name = "purchase_started"
        override val params = mapOf(
            P.SURFACE to surface.value,
            P.PRODUCT_ID to productId,
            P.PRICE_MICROS to priceMicros,
            P.CURRENCY to currency,
        )
    }

    data class PurchaseCompleted(
        val surface: PurchaseSurface,
        val productId: String,
        val value: Double,
        val currency: String,
    ) : AnalyticsEvent {
        override val name = "purchase_completed"
        override val params = mapOf(
            P.SURFACE to surface.value,
            P.PRODUCT_ID to productId,
            P.VALUE to value,
            P.CURRENCY to currency,
        )
    }

    /**
     * Standardowy event GA4, wysyłany razem z [PurchaseCompleted]. Implementacja Firebase dokłada
     * do niego tablicę `items` — tego jednego pola nie da się wyrazić mapą parametrów.
     */
    data class PurchaseStandard(
        val productId: String,
        val value: Double,
        val currency: String,
    ) : AnalyticsEvent {
        override val name = "purchase"
        override val params = mapOf(
            P.VALUE to value,
            P.CURRENCY to currency,
        )
    }

    data class PurchasePending(
        val surface: PurchaseSurface,
        val productId: String,
    ) : AnalyticsEvent {
        override val name = "purchase_pending"
        override val params = mapOf(P.SURFACE to surface.value, P.PRODUCT_ID to productId)
    }

    data class PurchaseCancelled(
        val surface: PurchaseSurface,
        val productId: String,
    ) : AnalyticsEvent {
        override val name = "purchase_cancelled"
        override val params = mapOf(P.SURFACE to surface.value, P.PRODUCT_ID to productId)
    }

    data class PurchaseFailed(
        val surface: PurchaseSurface,
        val productId: String,
        val errorCode: String,
    ) : AnalyticsEvent {
        override val name = "purchase_failed"
        override val params = mapOf(
            P.SURFACE to surface.value,
            P.PRODUCT_ID to productId,
            P.ERROR_CODE to errorCode,
        )
    }

    /**
     * Próba zakupu bez ProductDetails w cache. Łapie cichą utratę przychodu: użytkownik klika
     * "Kup" i nie dzieje się nic, bo launchBillingFlow po cichu wychodzi.
     */
    data class PaywallPriceMissing(
        val surface: PurchaseSurface,
        val productId: String,
    ) : AnalyticsEvent {
        override val name = "paywall_price_missing"
        override val params = mapOf(P.SURFACE to surface.value, P.PRODUCT_ID to productId)
    }

    data class TrialStarted(val mode: String) : AnalyticsEvent {
        override val name = "trial_started"
        override val params = mapOf(P.MODE to mode)
    }

    /** Wyczerpanie puli darmowych pytań. */
    data class TrialWallReached(
        val mode: String,
        val questionsAnswered: Int,
    ) : AnalyticsEvent {
        override val name = "trial_wall_reached"
        override val params = mapOf(P.MODE to mode, P.QUESTIONS_ANSWERED to questionsAnswered.toLong())
    }

    // endregion

    // region Użycie

    data class ScreenView(
        val screenName: String,
        val screenClass: String,
    ) : AnalyticsEvent {
        override val name = "screen_view"
        override val params = mapOf(P.SCREEN_NAME to screenName, P.SCREEN_CLASS to screenClass)
    }

    /** [locked] mierzy popyt na treść jeszcze niekupioną — inna diagnoza niż sam wolumen sprzedaży. */
    data class ModeSelected(
        val mode: String,
        val locked: Boolean,
    ) : AnalyticsEvent {
        override val name = "mode_selected"
        override val params = mapOf(P.MODE to mode, P.LOCKED to locked.asParam())
    }

    data class AddonTapped(
        val addon: HomeAddon,
        val available: Boolean,
    ) : AnalyticsEvent {
        override val name = "addon_tapped"
        override val params = mapOf(P.ADDON to addon.value, P.AVAILABLE to available.asParam())
    }

    data class CategorySelected(
        val mode: String,
        val categoryId: Long,
        val locked: Boolean,
    ) : AnalyticsEvent {
        override val name = "category_selected"
        override val params = mapOf(
            P.MODE to mode,
            P.CATEGORY_ID to categoryId.toString(),
            P.LOCKED to locked.asParam(),
        )
    }

    data class QuizStarted(
        val mode: String,
        val source: QuizSource,
        val questionsCount: Int,
        val isTrial: Boolean,
    ) : AnalyticsEvent {
        override val name = "quiz_started"
        override val params = mapOf(
            P.MODE to mode,
            P.SOURCE to source.value,
            P.QUESTIONS_COUNT to questionsCount.toLong(),
            P.IS_TRIAL to isTrial.asParam(),
        )
    }

    data class QuizFinished(
        val mode: String,
        val completion: QuizCompletion,
        val questionsAnswered: Int,
        val correctAnswers: Int,
        val durationSec: Long,
        val isTrial: Boolean,
    ) : AnalyticsEvent {
        override val name = "quiz_finished"
        override val params = mapOf(
            P.MODE to mode,
            P.COMPLETION to completion.value,
            P.QUESTIONS_ANSWERED to questionsAnswered.toLong(),
            P.CORRECT_ANSWERS to correctAnswers.toLong(),
            P.DURATION_SEC to durationSec,
            P.IS_TRIAL to isTrial.asParam(),
        )
    }

    data class RevisionsConfigured(
        val criterion: String,
        val mode: String,
        val categoriesCount: Int,
        val questionsCount: Int,
    ) : AnalyticsEvent {
        override val name = "revisions_configured"
        override val params = mapOf(
            P.CRITERION to criterion,
            P.MODE to mode,
            P.CATEGORIES_COUNT to categoriesCount.toLong(),
            P.QUESTIONS_COUNT to questionsCount.toLong(),
        )
    }

    data class OnboardingFinished(
        val skipped: Boolean,
        val lastPage: Int,
    ) : AnalyticsEvent {
        override val name = "onboarding_finished"
        override val params = mapOf(P.SKIPPED to skipped.asParam(), P.LAST_PAGE to lastPage.toLong())
    }

    data class ModeOnboardingFinished(val mode: String) : AnalyticsEvent {
        override val name = "mode_onboarding_finished"
        override val params = mapOf(P.MODE to mode)
    }

    // endregion

    // region Retencja

    data object RatingPromptShown : AnalyticsEvent {
        override val name = "rating_prompt_shown"
    }

    data class RatingPromptAnswered(
        val rating: Int,
        val action: RatingAction,
    ) : AnalyticsEvent {
        override val name = "rating_prompt_answered"
        override val params = mapOf(P.RATING to rating.toLong(), P.ACTION to action.value)
    }

    data object NotificationPromptShown : AnalyticsEvent {
        override val name = "notification_prompt_shown"
    }

    data class NotificationPromptAnswered(val action: NotificationPromptAction) : AnalyticsEvent {
        override val name = "notification_prompt_answered"
        override val params = mapOf(P.ACTION to action.value)
    }

    /** Nazwa z koncowka "-ed": `notification_open` jest zarezerwowane przez FCM. */
    data class NotificationOpened(val destination: String) : AnalyticsEvent {
        override val name = "notification_opened"
        override val params = mapOf(P.DESTINATION to destination)
    }

    data class NewsBannerDismissed(val bannerId: String) : AnalyticsEvent {
        override val name = "news_banner_dismissed"
        override val params = mapOf(P.BANNER_ID to bannerId)
    }

    data class SignupCompleted(val method: AuthMethod) : AnalyticsEvent {
        override val name = "signup_completed"
        override val params = mapOf(P.METHOD to method.value)
    }

    data class IssueReported(val mode: String) : AnalyticsEvent {
        override val name = "issue_reported"
        override val params = mapOf(P.MODE to mode)
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
}
