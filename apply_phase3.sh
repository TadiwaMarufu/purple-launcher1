#!/bin/bash
set -e

echo "=== Applying Phase 3: App Drawer & Broadcast Receiver ==="

# 1. Update AppInfo model to include package categorization helper
cat << 'KOTLIN' > app/src/main/java/com/purple/launcher/apps/AppCategory.kt
package com.purple.launcher.apps

enum class AppCategory(val displayName: String) {
    ALL("All"),
    COMMUNICATION("Communication"),
    SOCIAL("Social"),
    MEDIA("Media"),
    WORK("Work"),
    GAMES("Games"),
    TOOLS("Tools")
}
KOTLIN

# 2. Update AppRepository with dynamic categorization logic and reactive listener support
cat << 'KOTLIN' > app/src/main/java/com/purple/launcher/apps/AppRepository.kt
package com.purple.launcher.apps

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppRepository(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager
    
    private val _appList = MutableStateFlow<List<AppInfo>>(emptyList())
    val appList: StateFlow<List<AppInfo>> = _appList.asStateFlow()

    fun reloadApps() {
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        
        val resolveInfos = packageManager.queryIntentActivities(mainIntent, 0)
        val apps = resolveInfos.mapNotNull { resolveInfo ->
            val pkgName = resolveInfo.activityInfo.packageName
            val label = resolveInfo.loadLabel(packageManager).toString()
            val icon = resolveInfo.loadIcon(packageManager)
            val category = categorizeApp(pkgName, resolveInfo.activityInfo.applicationInfo)

            AppInfo(
                packageName = pkgName,
                label = label,
                icon = icon,
                category = category
            )
        }.sortedBy { it.label.lowercase() }

        _appList.value = apps
    }

    private fun categorizeApp(packageName: String, appInfo: ApplicationInfo): AppCategory {
        val categoryAttr = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            appInfo.category
        } else {
            ApplicationInfo.CATEGORY_UNDEFINED
        }

        return when (categoryAttr) {
            ApplicationInfo.CATEGORY_AUDIO, ApplicationInfo.CATEGORY_VIDEO, ApplicationInfo.CATEGORY_IMAGE -> AppCategory.MEDIA
            ApplicationInfo.CATEGORY_GAME -> AppCategory.GAMES
            ApplicationInfo.CATEGORY_MAPS, ApplicationInfo.CATEGORY_PRODUCTIVITY -> AppCategory.WORK
            else -> inferCategoryFromPackage(packageName)
        }
    }

    private fun inferCategoryFromPackage(pkg: String): AppCategory {
        val lower = pkg.lowercase()
        return when {
            lower.contains("whatsapp") || lower.contains("telegram") || lower.contains("messenger") || lower.contains("sms") -> AppCategory.COMMUNICATION
            lower.contains("facebook") || lower.contains("twitter") || lower.contains("instagram") || lower.contains("tiktok") -> AppCategory.SOCIAL
            lower.contains("youtube") || lower.contains("spotify") || lower.contains("music") || lower.contains("player") -> AppCategory.MEDIA
            lower.contains("doc") || lower.contains("sheet") || lower.contains("office") || lower.contains("mail") -> AppCategory.WORK
            lower.contains("game") -> AppCategory.GAMES
            else -> AppCategory.TOOLS
        }
    }

    fun launchApp(packageName: String) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        }
    }
}
KOTLIN

# 3. Add AppInfo updated model
cat << 'KOTLIN' > app/src/main/java/com/purple/launcher/apps/AppInfo.kt
package com.purple.launcher.apps

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val label: String,
    val icon: Drawable,
    val category: AppCategory
)
KOTLIN

# 4. Create AppInstallReceiver for live package tracking
cat << 'KOTLIN' > app/src/main/java/com/purple/launcher/apps/AppInstallReceiver.kt
package com.purple.launcher.apps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AppInstallReceiver(private val onPackageChanged: () -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_PACKAGE_ADDED,
            Intent.ACTION_PACKAGE_REMOVED,
            Intent.ACTION_PACKAGE_REPLACED -> {
                onPackageChanged()
            }
        }
    }
}
KOTLIN

echo "=== Phase 3 Files Configured Successfully ==="
