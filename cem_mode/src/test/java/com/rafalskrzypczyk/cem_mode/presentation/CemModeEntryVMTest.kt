package com.rafalskrzypczyk.cem_mode.presentation

import com.rafalskrzypczyk.core.shared_prefs.SharedPreferencesApi
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class CemModeEntryVMTest {

    private lateinit var sharedPreferences: SharedPreferencesApi
    private lateinit var viewModel: CemModeEntryVM

    @Before
    fun setup() {
        sharedPreferences = mockk()
        viewModel = CemModeEntryVM(sharedPreferences)
    }

    @Test
    fun `isOnboardingSeen returns true when shared prefs has it as true`() {
        every { sharedPreferences.getCemModeOnboardingSeen() } returns true
        
        assert(viewModel.isOnboardingSeen())
    }

    @Test
    fun `isOnboardingSeen returns false when shared prefs has it as false`() {
        every { sharedPreferences.getCemModeOnboardingSeen() } returns false
        
        assert(!viewModel.isOnboardingSeen())
    }
}
