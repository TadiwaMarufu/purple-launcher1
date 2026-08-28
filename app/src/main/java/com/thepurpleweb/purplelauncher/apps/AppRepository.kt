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

        val pm =
            context.packageManager

        val intent =
            Intent(
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
