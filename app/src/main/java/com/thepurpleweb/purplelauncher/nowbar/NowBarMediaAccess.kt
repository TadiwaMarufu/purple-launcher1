package com.thepurpleweb.purplelauncher.nowbar

import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import com.thepurpleweb.purplelauncher.notifications.PurpleNotificationListenerService

object NowBarMediaAccess {

    fun isNotificationListenerEnabled(
        context: Context
    ): Boolean {

        val enabled =
            Settings.Secure.getString(
                context.contentResolver,
                "enabled_notification_listeners"
            )
                ?: return false

        val component =
            ComponentName(
                context,
                PurpleNotificationListenerService::class.java
            )

        return enabled
            .split(":")
            .any {
                it.equals(
                    component.flattenToString(),
                    ignoreCase = true
                )
            }
    }
}
