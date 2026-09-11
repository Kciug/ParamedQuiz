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
 * ApplicationNavigation.kt.
 */
class ScreenNamesTest {

    private inline fun <reified T : Any> route(): String = T::class.qualifiedName.orEmpty()

    @Test
    fun `every main route has a stable screen name`() {
        val expected = mapOf(
            route<Signup>() to "signup",
            route<DailyExercise>() to "daily_exercise",
            route<MainMenu>() to "home",
            route<UserPage>() to "user_page",
            route<UserSettings>() to "user_settings",
            route<NotificationSettings>() to "notification_settings",
            route<MainMode>() to "main_mode",
            route<SwipeMode>() to "swipe_mode",
            route<TranslationMode>() to "translation_mode",
            route<CemMode>() to "cem_mode",
            route<Onboarding>() to "onboarding",
            route<PrivacyConsent>() to "privacy_consent",
            route<Store>() to "store",
            route<Dev>() to "dev_options",
            route<TermsOfService>() to "terms_of_service",
            route<RevisionsMode>() to "revisions_mode",
        )

        expected.forEach { (route, name) ->
            assertEquals(route, name, ScreenNames.screenNameFor(route))
        }
    }

    @Test
    fun `route arguments do not change the screen name`() {
        assertEquals("swipe_mode", ScreenNames.screenNameFor(route<SwipeMode>() + "?isTrial={isTrial}"))
        assertEquals("main_mode", ScreenNames.screenNameFor(route<MainMode>() + "/{categoryId}"))
    }

    @Test
    fun `an unmapped route reports unknown instead of leaking the class name`() {
        assertEquals(ScreenNames.UNKNOWN, ScreenNames.screenNameFor("com.example.SomethingNew"))
        assertEquals(ScreenNames.UNKNOWN, ScreenNames.screenNameFor(null))
    }

    /**
     * iOS rozroznia „quiz w trybie glownym" od „quiz w Swipe" parametrem `mode`, nie osobna nazwa
     * ekranu. Zadanie dnia to tresc trybu glownego; powtorki nie znaja trybu na poziomie trasy.
     */
    @Test
    fun `mode is set for mode roots only`() {
        assertEquals("main", ScreenNames.modeFor(route<MainMode>()))
        assertEquals("main", ScreenNames.modeFor(route<DailyExercise>()))
        assertEquals("swipe", ScreenNames.modeFor(route<SwipeMode>() + "?isTrial={isTrial}"))
        assertEquals("translations", ScreenNames.modeFor(route<TranslationMode>()))
        assertEquals("cem", ScreenNames.modeFor(route<CemMode>()))

        assertNull(ScreenNames.modeFor(route<MainMenu>()))
        assertNull(ScreenNames.modeFor(route<RevisionsMode>()))
        assertNull(ScreenNames.modeFor(route<Store>()))
    }

    /** Kontrakt iOS: `screen_class` jest z definicji rowne `screen_name`. */
    @Test
    fun `screen class equals screen name and mode is omitted when unknown`() {
        val plain = AnalyticsEvent.ScreenView(screenName = "home")
        assertEquals("home", plain.params["screen_class"])
        assertEquals(null, plain.params["mode"])

        val withMode = AnalyticsEvent.ScreenView(screenName = "swipe_mode", mode = "swipe")
        assertEquals("swipe_mode", withMode.params["screen_class"])
        assertEquals("swipe", withMode.params["mode"])
    }
}
