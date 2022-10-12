package com.example.bledemoapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object AppUtils {

    /**
     * Check whether user is currently connected to internet or not
     * @return true if user has network connectivity else false
     */
    fun isConnected(context: Context): Boolean {
        var isConnected = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        connectivityManager?.run {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
                when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        isConnected = true
                    }
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        isConnected = true
                    }
                    hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> {
                        isConnected = true
                    }
                }
            }
        }
        return isConnected
    }
}