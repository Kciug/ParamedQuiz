package com.rafalskrzypczyk.paramedquiz.analytics

import com.rafalskrzypczyk.paramedquiz.navigation.CemMode
import com.rafalskrzypczyk.paramedquiz.navigation.DailyExercise
import com.rafalskrzypczyk.paramedquiz.navigation.Dev
import com.rafalskrzypczyk.paramedquiz.navigation.MainMenu
import com.rafalskrzypczyk.paramedquiz.navigation.MainMode
import com.rafalskrzypczyk.paramedquiz.navigation.NotificationSettings
import com.rafalskrzypczyk.paramedquiz.navigation.Onboarding
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
 * zdarzeniami (`quiz_started`, `category_selected`), które niosą więcej informacji.
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
        qualifiedName<Store>() to "store",
        qualifiedName<Dev>() to "dev_options",
        qualifiedName<TermsOfService>() to "terms_of_service",
        qualifiedName<RevisionsMode>() to "revisions_mode",
    )

    fun screenNameFor(route: String?): String = byRoute[normalize(route)] ?: UNKNOWN

    fun screenClassFor(route: String?): String = normalize(route)?.substringAfterLast('.') ?: UNKNOWN

    /** Obcina argumenty trasy: `...SwipeMode?isTrial={isTrial}` oraz `.../{categoryId}`. */
    private fun normalize(route: String?): String? =
        route?.substringBefore('/')?.substringBefore('?')?.takeIf { it.isNotBlank() }

    private inline fun <reified T : Any> qualifiedName(): String = T::class.qualifiedName.orEmpty()
}
