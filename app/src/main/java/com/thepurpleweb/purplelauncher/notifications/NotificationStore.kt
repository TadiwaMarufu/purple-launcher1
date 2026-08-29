package com.thepurpleweb.purplelauncher.notifications

import android.content.Context
import android.content.Intent
import android.service.notification.StatusBarNotification

/**
 * Lightweight in-process notification store.
 *
 * Android remains responsible for the underlying notification system.
 * Purple Launcher only maintains a presentation-friendly snapshot.
 */
object NotificationStore {

    private val lock = Any()
    private val notifications = LinkedHashMap<String, NotificationItem>()

    const val ACTION_NOTIFICATIONS_CHANGED =
        "com.thepurpleweb.purplelauncher.NOTIFICATIONS_CHANGED"

    fun replaceAll(
        context: Context,
        items: List<NotificationItem>
    ) {
        synchronized(lock) {
            notifications.clear()

            items.sortedByDescending { it.timestamp }
                .forEach {
                    notifications[it.key] = it
                }
        }

        broadcast(context)
    }

    fun add(
        context: Context,
        item: NotificationItem
    ) {
        synchronized(lock) {
            notifications[item.key] = item
        }

        broadcast(context)
    }

    fun remove(
        context: Context,
        key: String
    ) {
        synchronized(lock) {
            notifications.remove(key)
        }

        broadcast(context)
    }

    fun all(): List<NotificationItem> {
        return synchronized(lock) {
            notifications.values
                .sortedByDescending { it.timestamp }
                .toList()
        }
    }

    fun clear() {
        synchronized(lock) {
            notifications.clear()
        }
    }

    private fun broadcast(context: Context) {
        context.sendBroadcast(
            Intent(ACTION_NOTIFICATIONS_CHANGED)
                .setPackage(context.packageName)
        )
    }

    fun fromStatusBarNotification(
        context: Context,
        sbn: StatusBarNotification
    ): NotificationItem? {

        val notification = sbn.notification
        val extras = notification.extras ?: return null

        val packageName = sbn.packageName

        val appName = try {
            context.packageManager
                .getApplicationLabel(
                    context.packageManager.getApplicationInfo(
                        packageName,
                        0
                    )
                )
                .toString()
        } catch (_: Exception) {
            packageName
        }

        val title =
            extras.getCharSequence("android.title")
                ?.toString()
                ?.trim()
                .orEmpty()

        val text =
            extras.getCharSequence("android.text")
                ?.toString()
                ?.trim()
                .orEmpty()

        val icon = try {
            context.packageManager
                .getApplicationIcon(packageName)
        } catch (_: Exception) {
            null
        }

        return NotificationItem(
            key = sbn.key,
            packageName = packageName,
            appName = appName,
            title = title,
            text = text,
            icon = icon,
            timestamp = sbn.postTime
        )
    }
}
