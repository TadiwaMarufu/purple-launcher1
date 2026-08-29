package com.thepurpleweb.purplelauncher.notifications

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class PurpleNotificationListenerService :
    NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()

        refreshNotifications()
    }

    override fun onNotificationPosted(
        sbn: StatusBarNotification
    ) {
        val item =
            NotificationStore.fromStatusBarNotification(
                this,
                sbn
            )

        if (item != null) {
            NotificationStore.add(
                this,
                item
            )
        }
    }

    override fun onNotificationRemoved(
        sbn: StatusBarNotification
    ) {
        NotificationStore.remove(
            this,
            sbn.key
        )
    }

    private fun refreshNotifications() {

        val items =
            activeNotifications
                ?.mapNotNull {
                    NotificationStore.fromStatusBarNotification(
                        this,
                        it
                    )
                }
                ?: emptyList()

        NotificationStore.replaceAll(
            this,
            items
        )
    }

    fun cancelNotificationFromLauncher(
        key: String
    ) {
        cancelNotification(key)
    }
}
