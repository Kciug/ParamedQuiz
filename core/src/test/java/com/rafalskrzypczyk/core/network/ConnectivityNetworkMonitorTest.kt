package com.rafalskrzypczyk.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ConnectivityNetworkMonitorTest {

    private val connectivityManager: ConnectivityManager = mockk(relaxed = true)
    private val context: Context = mockk<Context>().also {
        every { it.getSystemService(ConnectivityManager::class.java) } returns connectivityManager
    }
    private val monitor = ConnectivityNetworkMonitor(context)

    private fun capabilities(internet: Boolean, validated: Boolean): NetworkCapabilities =
        mockk<NetworkCapabilities>().also {
            every { it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns internet
            every { it.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns validated
        }

    private fun withActiveNetwork(capabilities: NetworkCapabilities?) {
        val network: Network = mockk(relaxed = true)
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
    }

    @Test
    fun `reports online for a validated internet connection`() {
        withActiveNetwork(capabilities(internet = true, validated = true))

        assertTrue(monitor.isOnline())
    }

    @Test
    fun `reports offline when there is no active network`() {
        every { connectivityManager.activeNetwork } returns null

        assertFalse(monitor.isOnline())
    }

    @Test
    fun `reports offline for a connection that has not been validated`() {
        withActiveNetwork(capabilities(internet = true, validated = false))

        assertFalse(monitor.isOnline())
    }

    @Test
    fun `reports offline for a network without internet capability`() {
        withActiveNetwork(capabilities(internet = false, validated = true))

        assertFalse(monitor.isOnline())
    }

    @Test
    fun `fails open when connectivity service is unavailable`() {
        val contextWithoutService: Context = mockk<Context>().also {
            every { it.getSystemService(ConnectivityManager::class.java) } returns null
        }

        assertTrue(ConnectivityNetworkMonitor(contextWithoutService).isOnline())
    }
}
