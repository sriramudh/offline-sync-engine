package com.sriramu.syncengine.network

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.annotation.RequiresPermission

internal class NetworkMonitor(
    context: Context,
    private val onConnected: () -> Unit
) {

    private val connectivityManager =
        context.applicationContext
            .getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager

    private var callback:
            ConnectivityManager.NetworkCallback? =
        null

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun startMonitoring() {

        if (callback != null) return

        callback =
            object :
                ConnectivityManager.NetworkCallback() {

                @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
                override fun onAvailable(
                    network: Network
                ) {

                    if (isConnected()) {
                        onConnected()
                    }
                }
            }

        connectivityManager
            .registerNetworkCallback(
                NetworkRequest.Builder()
                    .build(),
                callback!!
            )
    }

    fun stopMonitoring() {

        callback?.let {

            connectivityManager
                .unregisterNetworkCallback(
                    it
                )
        }

        callback = null
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isConnected():
            Boolean {

        val network =
            connectivityManager
                .activeNetwork
                ?: return false

        val capabilities =
            connectivityManager
                .getNetworkCapabilities(
                    network
                )
                ?: return false

        return capabilities.hasCapability(
            NetworkCapabilities
                .NET_CAPABILITY_INTERNET
        )
    }
}