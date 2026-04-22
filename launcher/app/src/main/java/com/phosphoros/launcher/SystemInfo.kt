package com.phosphoros.launcher

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager

data class SystemSnapshot(
    val batteryPercent: Int?,
    val network: String,
)

object SystemInfo {
    fun snapshot(context: Context): SystemSnapshot {
        return SystemSnapshot(
            batteryPercent = batteryPercent(context),
            network = networkLabel(context),
        )
    }

    private fun batteryPercent(context: Context): Int? {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)) ?: return null
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level < 0 || scale <= 0) return null
        return ((level * 100f) / scale).toInt()
    }

    private fun networkLabel(context: Context): String {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetwork ?: return "OFFLINE"
        val caps = cm.getNetworkCapabilities(net) ?: return "OFFLINE"
        return when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WIFI"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "LTE"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ETH"
            else -> "ONLINE"
        }
    }
}

