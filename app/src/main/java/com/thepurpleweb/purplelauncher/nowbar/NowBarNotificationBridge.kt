package com.thepurpleweb.purplelauncher.nowbar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.thepurpleweb.purplelauncher.notifications.NotificationItem
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
        if (!isListening) {
            val filter = IntentFilter(NotificationStore.ACTION_NOTIFICATIONS_CHANGED)
            context.registerReceiver(receiver, filter)
            isListening = true
            processNotifications()
        }
    }

    fun stop() {
        if (isListening) {
            try {
                context.unregisterReceiver(receiver)
            } catch (_: Exception) {}
            isListening = false
        }
    }

    private fun processNotifications() {
        val active = NotificationStore.getNotifications()

        val latestNav = active.firstOrNull { it.packageName in navPackages }
        if (latestNav != null) {
            coordinator.setNavigation(
                NowBarItem(
                    type = NowBarType.NAVIGATION,
                    title = latestNav.title.ifEmpty { "Navigation" },
                    subtitle = latestNav.content
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
                    subtitle = latestDownload.content
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
                    title = latestGeneral.title.ifEmpty { latestGeneral.packageName },
                    subtitle = latestGeneral.content
                )
            )
        } else {
            coordinator.setNotification(null)
        }
    }
}
