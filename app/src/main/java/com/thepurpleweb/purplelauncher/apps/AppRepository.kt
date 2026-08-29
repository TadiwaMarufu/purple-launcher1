package com.thepurpleweb.purplelauncher.apps

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

        val apps =
            pm.queryIntentActivities(
                intent,
                0
            )
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

    /**
     * Same query as getAllLaunchableApps, but performed off the main
     * thread. PackageManager queries and icon loading for a large app
     * list are real main-thread cost on low-RAM devices (spec section
     * 17) — this is the entry point every Activity should call instead
     * of the synchronous version, except where a cache hit is already
     * guaranteed cheap.
     */
    suspend fun getAllLaunchableAppsAsync(
        forceRefresh: Boolean = false
    ): List<AppInfo> = withContext(Dispatchers.Default) {
        getAllLaunchableApps(forceRefresh)
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
                apps.filter(::isCommunication)

            AppCategory.SOCIAL ->
                apps.filter(::isSocial)

            AppCategory.MEDIA ->
                apps.filter(::isMedia)

            AppCategory.WORK ->
                apps.filter(::isWork)

            AppCategory.GAMES ->
                apps.filter(::isGame)

            AppCategory.TOOLS ->
                apps.filter(::isTool)

            AppCategory.OTHER ->
                apps.filter { app ->
                    !isCommunication(app) &&
                    !isSocial(app) &&
                    !isMedia(app) &&
                    !isWork(app) &&
                    !isGame(app) &&
                    !isTool(app)
                }
        }
    }

    /**
     * Off-main-thread version of getAppsByCategory, for the same
     * reason as getAllLaunchableAppsAsync.
     */
    suspend fun getAppsByCategoryAsync(
        category: AppCategory
    ): List<AppInfo> = withContext(Dispatchers.Default) {
        getAppsByCategory(category)
    }

    private fun isCommunication(
        app: AppInfo
    ): Boolean {
        return containsAny(
            app,
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
        return containsAny(
            app,
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
        return containsAny(
            app,
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
        return containsAny(
            app,
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
        return containsAny(
            app,
            "game",
            "games"
        )
    }

    private fun isTool(
        app: AppInfo
    ): Boolean {
        return containsAny(
            app,
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
        app: AppInfo,
        vararg values: String
    ): Boolean {

        val text =
            "${app.label} ${app.packageName}"
                .lowercase()

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
