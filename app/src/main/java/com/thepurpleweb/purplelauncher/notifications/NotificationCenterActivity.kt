package com.thepurpleweb.purplelauncher.notifications

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.thepurpleweb.purplelauncher.R
import com.thepurpleweb.purplelauncher.profile.Profile
import com.thepurpleweb.purplelauncher.profile.ProfileEngine
import java.text.DateFormat
import java.util.Date

import android.service.notification.NotificationListenerService
class NotificationCenterActivity :
    AppCompatActivity() {

    private lateinit var profileEngine: ProfileEngine
    private lateinit var content: LinearLayout

    private val notificationReceiver =
        object : BroadcastReceiver() {

            override fun onReceive(
                context: Context?,
                intent: Intent?
            ) {
                render()
            }
        }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        profileEngine =
            ProfileEngine(
                applicationContext
            )

        buildUi()
        render()
    }

    override fun onStart() {
        super.onStart()

        registerReceiver(
            notificationReceiver,
            IntentFilter(
                NotificationStore.ACTION_NOTIFICATIONS_CHANGED
            )
        )

        render()
    }

    override fun onStop() {
        super.onStop()

        try {
            unregisterReceiver(
                notificationReceiver
            )
        } catch (_: Exception) {
        }
    }

    private fun buildUi() {

        val root =
            LinearLayout(this).apply {
                orientation =
                    LinearLayout.VERTICAL

                setBackgroundColor(
                    backgroundColor(
                        profileEngine.current.value
                    )
                )

                setPadding(
                    dp(20),
                    dp(28),
                    dp(20),
                    dp(20)
                )
            }

        val header =
            LinearLayout(this).apply {
                orientation =
                    LinearLayout.HORIZONTAL

                gravity =
                    Gravity.CENTER_VERTICAL

                layoutParams =
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
            }

        val title =
            TextView(this).apply {
                text = "Notifications"
                textSize = titleSize(
                    profileEngine.current.value
                )
                setTextColor(
                    textColor(
                        profileEngine.current.value
                    )
                )

                layoutParams =
                    LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                    )
            }

        val clear =
            TextView(this).apply {
                text = "Clear"
                textSize = 13f
                setTextColor(
                    accentColor(
                        profileEngine.current.value
                    )
                )

                setPadding(
                    dp(12),
                    dp(8),
                    dp(4),
                    dp(8)
                )

                setOnClickListener {
                    clearNotifications()
                }
            }

        header.addView(title)
        header.addView(clear)

        content =
            LinearLayout(this).apply {
                orientation =
                    LinearLayout.VERTICAL
            }

        val scroll =
            ScrollView(this).apply {
                layoutParams =
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1f
                    )

                addView(
                    content,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                )
            }

        root.addView(header)

        root.addView(
            TextView(this).apply {
                text = profileDescription(
                    profileEngine.current.value
                )
                textSize = 13f
                setTextColor(
                    Color.rgb(
                        145,
                        145,
                        145
                    )
                )

                setPadding(
                    0,
                    dp(4),
                    0,
                    dp(18)
                )
            }
        )

        root.addView(scroll)

        setContentView(root)
    }

    private fun render() {

        if (!::content.isInitialized) {
            return
        }

        content.removeAllViews()

        val notifications =
            NotificationStore.all()

        if (!hasNotificationAccess()) {
            showAccessRequired()
            return
        }

        if (notifications.isEmpty()) {
            showEmptyState()
            return
        }

        notifications.forEach { item ->
            addNotification(item)
        }
    }

    private fun addNotification(
        item: NotificationItem
    ) {

        val profile =
            profileEngine.current.value

        val card =
            LinearLayout(this).apply {
                orientation =
                    LinearLayout.HORIZONTAL

                gravity =
                    Gravity.CENTER_VERTICAL

                setPadding(
                    dp(cardPadding(profile)),
                    dp(cardPadding(profile)),
                    dp(cardPadding(profile)),
                    dp(cardPadding(profile))
                )

                background =
                    cardBackground(profile)

                layoutParams =
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin =
                            dp(cardSpacing(profile))
                    }

                setOnClickListener {
                    openNotification(item)
                }

                setOnLongClickListener {
                    dismissNotification(item)
                    true
                }
            }

        val icon =
            ImageView(this).apply {

                layoutParams =
                    LinearLayout.LayoutParams(
                        dp(iconSize(profile)),
                        dp(iconSize(profile))
                    )

                setImageDrawable(item.icon)
            }

        val text =
            LinearLayout(this).apply {
                orientation =
                    LinearLayout.VERTICAL

                setPadding(
                    dp(12),
                    0,
                    0,
                    0

                )

                layoutParams =
                    LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                    )
            }

        val app =
            TextView(this).apply {
                this.text = item.appName
                textSize = 11f
                setTextColor(
                    accentColor(profile)
                )
            }

        val title =
            TextView(this).apply {
                this.text =
                    item.title.ifEmpty {
                        item.appName
                    }

                textSize = 16f
                setTextColor(
                    textColor(profile)
                )

                maxLines = 2
            }

        val body =
            TextView(this).apply {
                this.text = item.text
                textSize = 13f
                setTextColor(
                    secondaryTextColor(profile)
                )

                maxLines = 3
            }

        val time =
            TextView(this).apply {
                this.text =
                    DateFormat
                        .getTimeInstance(
                            DateFormat.SHORT
                        )
                        .format(
                            Date(item.timestamp)
                        )

                textSize = 10f
                setTextColor(
                    Color.rgb(
                        110,
                        110,
                        110
                    )
                )
            }

        text.addView(app)
        text.addView(title)

        if (item.text.isNotEmpty()) {
            text.addView(body)
        }

        text.addView(time)

        card.addView(icon)
        card.addView(text)

        content.addView(card)
    }

    private fun showAccessRequired() {

        content.addView(
            TextView(this).apply {
                text =
                    "Purple Launcher needs Notification Access to present your notifications here."

                textSize = 16f
                setTextColor(Color.WHITE)

                setPadding(
                    dp(8),
                    dp(40),
                    dp(8),
                    dp(20)
                )
            }
        )

        content.addView(
            TextView(this).apply {
                text = "Open Notification Access"
                textSize = 15f
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)

                background =
                    cardBackground(
                        profileEngine.current.value
                    )

                setPadding(
                    dp(18),
                    dp(16),
                    dp(18),
                    dp(16)
                )

                setOnClickListener {
                    try {
                        startActivity(
                            Intent(
                                Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
                            )
                        )
                    } catch (_: Exception) {
                    }
                }
            }
        )
    }

    private fun showEmptyState() {

        val message =
            when (
                profileEngine.current.value
            ) {

                Profile.Fluid ->
                    "Everything is quiet."

                Profile.Premium ->
                    "No notifications."

                Profile.Calm ->
                    "Nothing needs your attention."

                Profile.Focus ->
                    "You're clear. Stay focused."

                Profile.Expressive ->
                    "The signal is quiet."
            }

        content.addView(
            TextView(this).apply {
                text = message
                textSize = 18f
                gravity = Gravity.CENTER
                setTextColor(
                    secondaryTextColor(
                        profileEngine.current.value
                    )
                )

                layoutParams =
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(180)
                    )
            }
        )
    }

    private fun openNotification(
        item: NotificationItem
    ) {

        try {
            val sbn =
                NotificationStore.all()
                    .firstOrNull {
                        it.key == item.key
                    }

            val serviceIntent =
                Intent(
                    this,
                    PurpleNotificationListenerService::class.java
                )

            // NotificationListenerService owns the actual notification
            // interaction. Launching the original PendingIntent is
            // intentionally delegated to Android.
            val listener =
                getSystemService(
                    NotificationListenerService::class.java
                )

            if (listener != null) {
                val active =
                    listener.activeNotifications
                        ?.firstOrNull {
                            it.key == item.key
                        }

                active?.notification
                    ?.contentIntent
                    ?.send()
            }

        } catch (_: Exception) {
        }
    }

    private fun dismissNotification(
        item: NotificationItem
    ) {

        try {
            val listener =
                getSystemService(
                    NotificationListenerService::class.java
                )

            if (listener != null) {
                listener.cancelNotification(
                    item.key
                )
            }

            NotificationStore.remove(
                this,
                item.key
            )

        } catch (_: Exception) {
        }
    }

    private fun clearNotifications() {

        val notifications =
            NotificationStore.all()

        if (notifications.isEmpty()) {
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Clear notifications?")
            .setMessage(
                "This removes the notifications currently presented by Purple Launcher."
            )
            .setPositiveButton("Clear") { _, _ ->

                try {
                    val listener =
                        getSystemService(
                            NotificationListenerService::class.java
                        )

                    notifications.forEach {
                        try {
                            listener?.cancelNotification(
                                it.key
                            )
                        } catch (_: Exception) {
                        }
                    }
                } catch (_: Exception) {
                }

                NotificationStore.clear()
                render()
            }
            .setNegativeButton(
                "Cancel",
                null
            )
            .show()
    }

    private fun hasNotificationAccess(): Boolean {

        val enabled =
            Settings.Secure.getString(
                contentResolver,
                "enabled_notification_listeners"
            ) ?: return false

        return enabled.contains(
            ComponentName(
                this,
                PurpleNotificationListenerService::class.java
            ).flattenToString()
        )
    }

    private fun backgroundColor(
        profile: Profile
    ): Int =
        when (profile) {
            Profile.Fluid ->
                Color.rgb(15, 13, 22)

            Profile.Premium ->
                Color.rgb(16, 16, 18)

            Profile.Calm ->
                Color.rgb(14, 15, 15)

            Profile.Focus ->
                Color.rgb(13, 16, 19)

            Profile.Expressive ->
                Color.rgb(20, 12, 24)
        }

    private fun textColor(
        profile: Profile
    ): Int =
        when (profile) {
            Profile.Fluid ->
                Color.rgb(245, 240, 255)

            Profile.Premium ->
                Color.rgb(245, 245, 245)

            Profile.Calm ->
                Color.rgb(225, 225, 225)

            Profile.Focus ->
                Color.rgb(240, 245, 248)

            Profile.Expressive ->
                Color.rgb(255, 242, 255)
        }

    private fun secondaryTextColor(
        profile: Profile
    ): Int =
        when (profile) {
            Profile.Fluid ->
                Color.rgb(175, 165, 190)

            Profile.Premium ->
                Color.rgb(165, 165, 170)

            Profile.Calm ->
                Color.rgb(145, 145, 145)

            Profile.Focus ->
                Color.rgb(155, 165, 172)

            Profile.Expressive ->
                Color.rgb(185, 160, 195)
        }

    private fun accentColor(
        profile: Profile
    ): Int =
        when (profile) {
            Profile.Fluid ->
                Color.rgb(190, 145, 255)

            Profile.Premium ->
                Color.rgb(205, 190, 165)

            Profile.Calm ->
                Color.rgb(175, 175, 175)

            Profile.Focus ->
                Color.rgb(125, 190, 255)

            Profile.Expressive ->
                Color.rgb(220, 125, 255)
        }

    private fun cardBackground(
        profile: Profile
    ): GradientDrawable {

        val color =
            when (profile) {
                Profile.Fluid ->
                    Color.argb(
                        48,
                        150,
                        100,
                        220
                    )

                Profile.Premium ->
                    Color.argb(
                        28,
                        255,
                        255,
                        255
                    )

                Profile.Calm ->
                    Color.argb(
                        20,
                        255,
                        255,
                        255
                    )

                Profile.Focus ->
                    Color.argb(
                        32,
                        90,
                        150,
                        190
                    )

                Profile.Expressive ->
                    Color.argb(
                        55,
                        170,
                        70,
                        220
                    )
            }

        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(16).toFloat()
        }
    }

    private fun profileDescription(
        profile: Profile
    ): String =
        when (profile) {
            Profile.Fluid ->
                "Notifications move with the environment."

            Profile.Premium ->
                "A refined view of what needs your attention."

            Profile.Calm ->
                "Only what matters, without the noise."

            Profile.Focus ->
                "Useful information first. Distractions second."

            Profile.Expressive ->
                "Your notifications, presented with character."
        }

    private fun titleSize(
        profile: Profile
    ): Float =
        when (profile) {
            Profile.Fluid -> 29f
            Profile.Premium -> 27f
            Profile.Calm -> 25f
            Profile.Focus -> 28f
            Profile.Expressive -> 31f
        }

    private fun cardPadding(
        profile: Profile
    ): Int =
        when (profile) {
            Profile.Fluid -> 14
            Profile.Premium -> 16
            Profile.Calm -> 14
            Profile.Focus -> 12
            Profile.Expressive -> 16
        }

    private fun cardSpacing(
        profile: Profile
    ): Int =
        when (profile) {
            Profile.Fluid -> 12
            Profile.Premium -> 10
            Profile.Calm -> 8
            Profile.Focus -> 6
            Profile.Expressive -> 14
        }

    private fun iconSize(
        profile: Profile
    ): Int =
        when (profile) {
            Profile.Fluid -> 46
            Profile.Premium -> 44
            Profile.Calm -> 40
            Profile.Focus -> 40
            Profile.Expressive -> 48
        }

    private fun dp(
        value: Int
    ): Int =
        (
            value *
                resources.displayMetrics.density
            ).toInt()
}
