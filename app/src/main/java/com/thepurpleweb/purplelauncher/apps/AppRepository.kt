package com.thepurpleweb.purplelauncher.apps

import android.content.Context
import android.content.Intent

class AppRepository(
    private val context: Context
) {

    private var cachedApps: List<AppInfo>? = null

    fun getAllLaunchableApps(
        forceRefresh: Boolean = false
    ): List<AppInfo> {

        if (!forceRefresh) {
            cachedApps?.let { return it }
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
        category: AppCategory
    ): List<AppInfo> {

        val apps =
            getAllLaunchableApps()

        return when (category) {

            AppCategory.ALL ->
                apps

            AppCategory.COMMUNICATION ->
                apps.filter {
                    isCommunication(it)
                }

            AppCategory.SOCIAL ->
                apps.filter {
                    isSocial(it)
                }

            AppCategory.MEDIA ->
                apps.filter {
                    isMedia(it)
                }

            AppCategory.WORK ->
                apps.filter {
                    isWork(it)
                }

            AppCategory.GAMES ->
                apps.filter {
                    isGame(it)
                }

            AppCategory.TOOLS ->
                apps.filter {
                    isTool(it)
                }
        }
    }

    private fun isCommunication(
        app: AppInfo
    ): Boolean {

        val text =
            "${app.label} ${app.packageName}"
                .lowercase()

        return containsAny(
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
    }

    private fun isSocial(
        app: AppInfo
    ): Boolean {

        val text =
            "${app.label} ${app.packageName}"
                .lowercase()

        return containsAny(
            text,
            "facebook",
            "instagram",
            "twitter",
            "tiktok",
            "reddit",
            "snapchat",
            "social"
        )
    }

    private fun isMedia(
        app: AppInfo
    ): Boolean {

        val text =
            "${app.label} ${app.packageName}"
                .lowercase()

        return containsAny(
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
    }

    private fun isWork(
        app: AppInfo
    ): Boolean {

        val text =
            "${app.label} ${app.packageName}"
                .lowercase()

        return containsAny(
            text,
            "office",
            "docs",
            "drive",
            "calendar",
            "slack",
            "teams",
            "notion",
            "work",
            "gmail"
        )
    }

    private fun isGame(
        app: AppInfo
    ): Boolean {

        val text =
            "${app.label} ${app.packageName}"
                .lowercase()

        return containsAny(
            text,
            "game",
            "games"
        )
    }

    private fun isTool(
        app: AppInfo
    ): Boolean {

        val text =
            "${app.label} ${app.packageName}"
                .lowercase()

        return containsAny(
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
