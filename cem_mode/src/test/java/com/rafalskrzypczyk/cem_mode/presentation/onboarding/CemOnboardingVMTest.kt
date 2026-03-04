package com.rafalskrzypczyk.cem_mode.presentation.onboarding

import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class CemOnboardingVMTest {

    private lateinit var sharedPreferences: SharedPreferencesApi
    private lateinit var viewModel: CemOnboardingVM

    @Before
    fun setup() {
        sharedPreferences = mockk(relaxed = true)
        viewModel = CemOnboardingVM(sharedPreferences)
    }

    @Test
    fun `finishOnboarding sets seen status and calls success callback`() {
        var called = false
        
        viewModel.finishOnboarding { called = true }
        
        verify { sharedPreferences.setCemModeOnboardingSeen(true) }
        assert(called)
    }
}
