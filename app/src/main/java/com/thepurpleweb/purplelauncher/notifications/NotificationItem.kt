package com.thepurpleweb.purplelauncher.notifications

import android.graphics.drawable.Drawable

data class NotificationItem(
    val key: String,
    val packageName: String,
    val appName: String,
    val title: String,
    val text: String,
    val icon: Drawable?,
    val timestamp: Long
)
