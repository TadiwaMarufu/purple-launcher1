package com.thepurpleweb.purplelauncher.apps

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

class AppRepository(context: Context) {

    private val appContext = context.applicationContext
    private val packageManager: PackageManager = appContext.packageManager
    private val categorizer = AppCategorizer()

    @Volatile
    private var cachedApps: List<AppInfo>? = null

    fun getAllLaunchableApps(forceRefresh: Boolean = false): List<AppInfo> {
        if (!forceRefresh) {
            cachedApps?.let { return it }
        }

        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val apps = packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_ALL)
            .asSequence()
            .map { resolveInfo ->
                AppInfo(
                    label = resolveInfo.loadLabel(packageManager).toString(),
                    packageName = resolveInfo.activityInfo.packageName,
                    icon = resolveInfo.loadIcon(packageManager)
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
            .toList()

        cachedApps = apps
        return apps
    }

    fun getAppsByCategory(
        category: AppCategory,
        forceRefresh: Boolean = false
    ): List<AppInfo> {
        val apps = getAllLaunchableApps(forceRefresh)

        if (category == AppCategory.ALL) {
            return apps
        }

        return apps.filter {
            categorizer.categorize(it) == category
        }
    }

    fun invalidateCache() {
        cachedApps = null
    }

    fun refresh(): List<AppInfo> {
        invalidateCache()
        return getAllLaunchableApps(true)
    }

    fun launchApp(packageName: String) {
        try {
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)

            launchIntent?.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                appContext.startActivity(this)
            }
        } catch (_: Exception) {
            // App may have disappeared between discovery and launch.
        }
    }
}
