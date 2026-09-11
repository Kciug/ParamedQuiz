package com.rafalskrzypczyk.paramedquiz.analytics

import com.rafalskrzypczyk.core.analytics.AnalyticsEvent
import com.rafalskrzypczyk.core.analytics.ScreenName
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
 * Słownik jest wspólny z iOS: `home`, `store`, `account`, `settings`, `notification_settings`.
 * Ekrany wewnątrz trybów (`categories`, `quiz`, `quiz_end`, `revision_setup`) raportują zagnieżdżone
 * `NavHost`y przez `TrackScreenViews` — korzenie trybów są tu **pomijane**, żeby wejście w tryb nie
 * dawało dwóch ekranów. `signup`, `onboarding`, `privacy_consent`, `terms_of_service` i `dev_options`
 * to nasze dodatki.
 */
object ScreenNames {
    const val UNKNOWN = "unknown"

    private val byRoute: Map<String, String> = mapOf(
        qualifiedName<Signup>() to "signup",
        qualifiedName<DailyExercise>() to ScreenName.QUIZ,
        qualifiedName<MainMenu>() to "home",
        qualifiedName<UserPage>() to "account",
        qualifiedName<UserSettings>() to "settings",
        qualifiedName<NotificationSettings>() to "notification_settings",
        qualifiedName<Onboarding>() to "onboarding",
        qualifiedName<PrivacyConsent>() to "privacy_consent",
        qualifiedName<Store>() to "store",
        qualifiedName<Dev>() to "dev_options",
        qualifiedName<TermsOfService>() to "terms_of_service",
    )

    /** Zadanie dnia to quiz z treścią trybu głównego — iOS nie ma dla niego osobnej nazwy ekranu. */
    private val modeByRoute: Map<String, String> = mapOf(
        qualifiedName<DailyExercise>() to QuizMode.MainMode.analyticsName(),
    )

    /** Korzenie zagnieżdżonych `NavHost`ów — ekran raportuje kontroler wewnętrzny, nie kontener. */
    private val containers: Set<String> = setOf(
        qualifiedName<MainMode>(),
        qualifiedName<SwipeMode>(),
        qualifiedName<TranslationMode>(),
        qualifiedName<CemMode>(),
        qualifiedName<RevisionsMode>(),
    )

    /** `null` dla kontenerów: ich ekrany raportują zagnieżdżone `NavHost`y. */
    fun screenViewFor(route: String?): AnalyticsEvent.ScreenView? {
        val key = normalize(route)
        if (key != null && key in containers) return null
        return AnalyticsEvent.ScreenView(
            screenName = byRoute[key] ?: UNKNOWN,
            mode = modeByRoute[key],
        )
    }

    fun isContainer(route: String?): Boolean = normalize(route) in containers

    /** Obcina argumenty trasy: `...SwipeMode?isTrial={isTrial}` oraz `.../{categoryId}`. */
    private fun normalize(route: String?): String? =
        route?.substringBefore('/')?.substringBefore('?')?.takeIf { it.isNotBlank() }

    private inline fun <reified T : Any> qualifiedName(): String = T::class.qualifiedName.orEmpty()
}
