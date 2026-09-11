package com.rafalskrzypczyk.paramedquiz.analytics

import com.rafalskrzypczyk.core.analytics.AnalyticsEvent
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Mapa tras jest jawna, wiec nowa trasa bez wpisu raportuje `unknown` po cichu. Ten test jest
 * jedynym miejscem, ktore to wylapie przed wydaniem — trzymamy w nim komplet tras z
 * ApplicationNavigation.kt: 11 ekranow + 5 kontenerow trybow.
 */
class ScreenNamesTest {

    private inline fun <reified T : Any> route(): String = T::class.qualifiedName.orEmpty()

    private fun nameFor(route: String?): String? = ScreenNames.screenViewFor(route)?.screenName

    @Test
    fun `every leaf route has a stable screen name from the shared dictionary`() {
        val expected = mapOf(
            route<Signup>() to "signup",
            route<DailyExercise>() to "quiz",
            route<MainMenu>() to "home",
            route<UserPage>() to "account",
            route<UserSettings>() to "settings",
            route<NotificationSettings>() to "notification_settings",
            route<Onboarding>() to "onboarding",
            route<PrivacyConsent>() to "privacy_consent",
            route<Store>() to "store",
            route<Dev>() to "dev_options",
            route<TermsOfService>() to "terms_of_service",
        )

        expected.forEach { (route, name) ->
            assertEquals(route, name, nameFor(route))
        }
    }

    /**
     * Wejscie w tryb otwiera zagniezdzony NavHost, ktory sam raportuje `categories`/`quiz`/
     * `revision_setup`. Korzen nie moze dawac drugiego ekranu.
     */
    @Test
    fun `mode containers are skipped so entering a mode reports one screen`() {
        listOf(
            route<MainMode>(),
            route<MainMode>() + "/{categoryId}",
            route<SwipeMode>() + "?isTrial={isTrial}",
            route<TranslationMode>(),
            route<CemMode>(),
            route<RevisionsMode>(),
        ).forEach { route ->
            assertNull(route, ScreenNames.screenViewFor(route))
        }
    }

    @Test
    fun `an unmapped route reports unknown instead of leaking the class name`() {
        assertEquals(ScreenNames.UNKNOWN, nameFor("com.example.SomethingNew"))
        assertEquals(ScreenNames.UNKNOWN, nameFor(null))
    }

    /** Zadanie dnia to quiz z trescia trybu glownego — iOS nie ma dla niego osobnej nazwy. */
    @Test
    fun `daily exercise is a main mode quiz`() {
        val screen = ScreenNames.screenViewFor(route<DailyExercise>())!!
        assertEquals("quiz", screen.screenName)
        assertEquals("main", screen.mode)
        assertNull(ScreenNames.screenViewFor(route<MainMenu>())!!.mode)
    }

    /** Kontrakt iOS: `screen_class` jest z definicji rowne `screen_name`. */
    @Test
    fun `screen class equals screen name and mode is omitted when unknown`() {
        val plain = AnalyticsEvent.ScreenView(screenName = "home")
        assertEquals("home", plain.params["screen_class"])
        assertEquals(null, plain.params["mode"])

        val withMode = AnalyticsEvent.ScreenView(screenName = "quiz", mode = "swipe")
        assertEquals("quiz", withMode.params["screen_class"])
        assertEquals("swipe", withMode.params["mode"])
    }
}
