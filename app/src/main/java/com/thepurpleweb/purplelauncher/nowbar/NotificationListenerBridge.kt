package com.thepurpleweb.purplelauncher.nowbar

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.thepurpleweb.purplelauncher.notifications.NotificationStore

class NotificationListenerBridge : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.let {
            val item = NotificationStore.fromStatusBarNotification(applicationContext, it)
            if (item != null) {
                NotificationStore.add(applicationContext, item)
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        sbn?.let {
            NotificationStore.remove(applicationContext, it.key)
        }
    }
}
