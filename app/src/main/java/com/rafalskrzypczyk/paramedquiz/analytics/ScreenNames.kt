package com.rafalskrzypczyk.paramedquiz.analytics

import com.rafalskrzypczyk.core.analytics.analyticsName
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.paramedquiz.navigation.CemMode
import com.rafalskrzypczyk.paramedquiz.navigation.DailyExercise
import com.rafalskrzypczyk.paramedquiz.navigation.Dev
import com.rafalskrzypczyk.paramedquiz.navigation.MainMenu
import com.rafalskrzypczyk.paramedquiz.navigation.MainMode
import com.rafalskrzypczyk.paramedquiz.navigation.NotificationSettings
import com.rafalskrzypczyk.paramedquiz.navigation.Onboarding
import com.rafalskrzypczyk.paramedquiz.navigation.PrivacyConsent
import com.rafalskrzypczyk.paramedquiz.navigation.RevisionsMode
import com.rafalskrzypczyk.paramedquiz.navigation.Signup
import com.rafalskrzypczyk.paramedquiz.navigation.Store
import com.rafalskrzypczyk.paramedquiz.navigation.SwipeMode
import com.rafalskrzypczyk.paramedquiz.navigation.TermsOfService
import com.rafalskrzypczyk.paramedquiz.navigation.TranslationMode
import com.rafalskrzypczyk.paramedquiz.navigation.UserPage
import com.rafalskrzypczyk.paramedquiz.navigation.UserSettings

/**
 * Mapa tras głównego `NavHost`a na stabilne nazwy ekranów dla zdarzenia `screen_view`.
 *
 * Trasy są typowane (kotlinx.serialization), więc `NavDestination.route` to pełna nazwa kwalifikowana
 * z ewentualnymi argumentami (`...navigation.SwipeMode?isTrial={isTrial}`). Skrót „ostatni segment
 * nazwy pakietu" jest zdradliwy: `Onboarding` jest zadeklarowane osobno w czterech modułach i
 * cztery różne ekrany scaliłyby się w jedną wartość. Stąd jawna mapa.
 *
 * Trasy zagnieżdżonych `NavHost`ów tu nie trafiają — mają własne kontrolery i są pokryte jawnymi
 * zdarzeniami (`quiz_start`, `category_select`), które niosą więcej informacji.
 */
object ScreenNames {
    const val UNKNOWN = "unknown"

    private val byRoute: Map<String, String> = mapOf(
        qualifiedName<Signup>() to "signup",
        qualifiedName<DailyExercise>() to "daily_exercise",
        qualifiedName<MainMenu>() to "home",
        qualifiedName<UserPage>() to "user_page",
        qualifiedName<UserSettings>() to "user_settings",
        qualifiedName<NotificationSettings>() to "notification_settings",
        qualifiedName<MainMode>() to "main_mode",
        qualifiedName<SwipeMode>() to "swipe_mode",
        qualifiedName<TranslationMode>() to "translation_mode",
        qualifiedName<CemMode>() to "cem_mode",
        qualifiedName<Onboarding>() to "onboarding",
        qualifiedName<PrivacyConsent>() to "privacy_consent",
        qualifiedName<Store>() to "store",
        qualifiedName<Dev>() to "dev_options",
        qualifiedName<TermsOfService>() to "terms_of_service",
        qualifiedName<RevisionsMode>() to "revisions_mode",
    )

    /**
     * Tryb dla korzeni trybow. iOS rozroznia „quiz w trybie glownym" od „quiz w Swipe" parametrem
     * `mode` na `screen_view`, nie osobna nazwa ekranu — bez tego parametru wspolny raport
     * ekranow nie da sie zlozyc. Zadanie dnia to tresc trybu glownego. Powtorki nie maja trybu
     * na poziomie trasy (tryb tresci jest znany dopiero wewnatrz).
     */
    private val modeByRoute: Map<String, String> = mapOf(
        qualifiedName<MainMode>() to QuizMode.MainMode.analyticsName(),
        qualifiedName<DailyExercise>() to QuizMode.MainMode.analyticsName(),
        qualifiedName<SwipeMode>() to QuizMode.SwipeMode.analyticsName(),
        qualifiedName<TranslationMode>() to QuizMode.TranslationMode.analyticsName(),
        qualifiedName<CemMode>() to QuizMode.CemMode.analyticsName(),
    )

    fun screenNameFor(route: String?): String = byRoute[normalize(route)] ?: UNKNOWN

    fun modeFor(route: String?): String? = modeByRoute[normalize(route)]

    /** Obcina argumenty trasy: `...SwipeMode?isTrial={isTrial}` oraz `.../{categoryId}`. */
    private fun normalize(route: String?): String? =
        route?.substringBefore('/')?.substringBefore('?')?.takeIf { it.isNotBlank() }

    private inline fun <reified T : Any> qualifiedName(): String = T::class.qualifiedName.orEmpty()
}
