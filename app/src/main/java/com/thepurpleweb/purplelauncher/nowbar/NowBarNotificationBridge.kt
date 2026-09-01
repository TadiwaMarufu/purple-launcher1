package com.thepurpleweb.purplelauncher.nowbar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.thepurpleweb.purplelauncher.notifications.NotificationStore

class NowBarNotificationBridge(
    private val context: Context,
    private val coordinator: NowBarCoordinator
) {
    private var isListening = false

    private val navPackages = setOf(
        "com.google.android.apps.maps",
        "com.waze",
        "com.here.app.maps"
    )

    private val downloadPackages = setOf(
        "com.android.providers.downloads",
        "com.google.android.providers.media.module"
    )

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            if (intent?.action == NotificationStore.ACTION_NOTIFICATIONS_CHANGED) {
                processNotifications()
            }
        }
    }

    fun start() {
        if (isListening) {
            return
        }

        try {
            // This is an internal, same-app broadcast (NotificationStore
            // sets setPackage(context.packageName)), so RECEIVER_NOT_EXPORTED
            // is correct here. Registering without an export flag throws a
            // SecurityException on API 33+ when targetSdk >= 33 — this
            // bridge previously had no protection against that.
            ContextCompat.registerReceiver(
                context,
                receiver,
                IntentFilter(NotificationStore.ACTION_NOTIFICATIONS_CHANGED),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            isListening = true
            processNotifications()
        } catch (_: Exception) {
            // Gracefully degrade: NOTIFICATION/DOWNLOAD/NAVIGATION Now Bar
            // types simply won't populate, rest of the launcher unaffected.
        }
    }

    fun stop() {
        if (isListening) {
            try {
                context.unregisterReceiver(receiver)
            } catch (_: Exception) {
            }
            isListening = false
        }
    }

    private fun processNotifications() {
        val active = NotificationStore.all()

        val latestNav = active.firstOrNull { it.packageName in navPackages }
        if (latestNav != null) {
            coordinator.setNavigation(
                NowBarItem(
                    type = NowBarType.NAVIGATION,
                    title = latestNav.title.ifEmpty { "Navigation" },
                    subtitle = latestNav.text
                )
            )
        } else {
            coordinator.setNavigation(null)
        }

        val latestDownload = active.firstOrNull {
            it.packageName in downloadPackages || it.title.contains("download", ignoreCase = true)
        }
        if (latestDownload != null) {
            coordinator.setDownload(
                NowBarItem(
                    type = NowBarType.DOWNLOAD,
                    title = latestDownload.title.ifEmpty { "Downloading" },
                    subtitle = latestDownload.text
                )
            )
        } else {
            coordinator.setDownload(null)
        }

        val latestGeneral = active.firstOrNull {
            it.packageName !in navPackages && it.packageName !in downloadPackages
        }
        if (latestGeneral != null) {
            coordinator.setNotification(
                NowBarItem(
                    type = NowBarType.NOTIFICATION,
                    title = latestGeneral.title.ifEmpty { latestGeneral.appName },
                    subtitle = latestGeneral.text
                )
            )
        } else {
            coordinator.setNotification(null)
        }
    }
}
