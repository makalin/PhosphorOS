package com.phosphoros.launcher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo

data class LaunchableApp(
    val label: String,
    val packageName: String,
    val activityName: String,
)

class AppIndex(context: Context) {
    private val pm: PackageManager = context.packageManager

    fun list(): List<LaunchableApp> {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        @Suppress("DEPRECATION")
        val infos: List<ResolveInfo> = pm.queryIntentActivities(intent, 0)
        return infos.mapNotNull { info ->
            val label = info.loadLabel(pm)?.toString()?.trim().orEmpty()
            val activity = info.activityInfo?.name ?: return@mapNotNull null
            val pkg = info.activityInfo?.packageName ?: return@mapNotNull null
            if (label.isBlank()) return@mapNotNull null
            LaunchableApp(label = label, packageName = pkg, activityName = activity)
        }.sortedBy { it.label.lowercase() }
    }
}

