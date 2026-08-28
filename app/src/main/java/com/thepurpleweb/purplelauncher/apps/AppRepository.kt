package com.thepurpleweb.purplelauncher.apps

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

class AppRepository(
    private val context: Context
) {

    private var cachedApps: List<AppInfo>? = null

    fun getAllLaunchableApps(
        forceRefresh: Boolean = false
    ): List<AppInfo> {

        if (!forceRefresh) {
            cachedApps?.let {
                return it
            }
        }

        val pm = context.packageManager

        val intent = Intent(
            Intent.ACTION_MAIN,
            null
        ).apply {
            addCategory(
                Intent.CATEGORY_LAUNCHER
            )
        }

        val resolveInfos =
            pm.queryIntentActivities(
                intent,
                0
            )

        val apps =
            resolveInfos
                .map { resolveInfo ->
                    AppInfo(
                        label =
                            resolveInfo
                                .loadLabel(pm)
                                .toString(),

                        packageName =
                            resolveInfo
                                .activityInfo
                                .packageName,

                        icon =
                            resolveInfo
                                .loadIcon(pm)
                    )
                }
                .distinctBy {
                    it.packageName
                }
                .sortedBy {
                    it.label.lowercase()
                }

        cachedApps = apps

        return apps
    }

    fun getAppsByCategory(
        category: String
    ): List<AppInfo> {

        val apps =
            getAllLaunchableApps()

        if (category.equals(
                "All",
                ignoreCase = true
            )
        ) {
            return apps
        }

        return apps.filter { app ->
            matchesCategory(
                app,
                category
            )
        }
    }

    private fun matchesCategory(
        app: AppInfo,
        category: String
    ): Boolean {

        val text =
            "${app.label} ${app.packageName}"
                .lowercase()

        return when (
            category.lowercase()
        ) {

            "communication" ->
                containsAny(
                    text,
                    "message",
                    "messaging",
                    "sms",
                    "dialer",
                    "phone",
                    "contact",
                    "whatsapp",
                    "telegram",
                    "signal",
                    "mail",
                    "email"
                )

            "social" ->
                containsAny(
                    text,
                    "facebook",
                    "instagram",
                    "twitter",
                    "tiktok",
                    "reddit",
                    "snapchat",
                    "social"
                )

            "media" ->
                containsAny(
                    text,
                    "spotify",
                    "youtube",
                    "music",
                    "video",
                    "camera",
                    "gallery",
                    "photos",
                    "netflix",
                    "media"
                )

            "work" ->
                containsAny(
                    text,
                    "office",
                    "docs",
                    "drive",
                    "calendar",
                    "slack",
                    "teams",
                    "notion",
                    "work",
                    "mail",
                    "gmail"
                )

            "games" ->
                containsAny(
                    text,
                    "game",
                    "games",
                    "play"
                )

            "tools" ->
                containsAny(
                    text,
                    "settings",
                    "calculator",
                    "clock",
                    "file",
                    "manager",
                    "browser",
                    "chrome",
                    "firefox",
                    "tool"
                )

            else ->
                apps
        }
    }

    private fun containsAny(
        text: String,
        vararg values: String
    ): Boolean {
        return values.any {
            text.contains(it)
        }
    }

    fun invalidateCache() {
        cachedApps = null
    }

    fun launchApp(
        packageName: String
    ) {

        val launchIntent =
            context
                .packageManager
                .getLaunchIntentForPackage(
                    packageName
                )

        launchIntent?.let {
            it.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
            )

            context.startActivity(it)
        }
    }
}
