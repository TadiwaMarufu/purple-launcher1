package com.thepurpleweb.purplelauncher.nowbar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import java.util.concurrent.ConcurrentHashMap

class NowBarCoordinator(
    context: Context,
    private val controller: NowBarController
) {

    private val appContext =
        context.applicationContext

    private val events =
        ConcurrentHashMap<NowBarType, NowBarEvent>()

    private var started =
        false

    private val batteryReceiver =
        object : BroadcastReceiver() {

            override fun onReceive(
                context: Context?,
                intent: Intent?
            ) {

                if (intent == null) {
                    return
                }

                updateBattery(intent)
            }
        }

    fun start() {

        if (started) {
            return
        }

        started = true

        try {

            appContext.registerReceiver(
                batteryReceiver,
                IntentFilter(
                    Intent.ACTION_BATTERY_CHANGED
                )
            )

        } catch (_: Exception) {
        }
    }

    fun stop() {

        if (!started) {
            return
        }

        started = false

        try {

            appContext.unregisterReceiver(
                batteryReceiver
            )

        } catch (_: Exception) {
        }
    }

    fun publish(
        item: NowBarItem,
        priority: Int
    ) {

        events[item.type] =
            NowBarEvent(
                item = item,
                priority = priority
            )

        render()
    }

    fun clear(
        type: NowBarType
    ) {

        events.remove(type)

        render()
    }

    fun clearTemporary() {

        val iterator =
            events.entries.iterator()

        while (iterator.hasNext()) {

            val entry =
                iterator.next()

            if (!entry.value.item.isPersistent) {
                iterator.remove()
            }
        }

        render()
    }

    fun setTimer(
        item: NowBarItem?
    ) {

        if (item == null) {

            events.remove(
                NowBarType.TIMER
            )

        } else {

            events[NowBarType.TIMER] =
                NowBarEvent(
                    item = item,
                    priority = PRIORITY_TIMER
                )
        }

        render()
    }

    fun setMedia(
        item: NowBarItem?
    ) {

        if (item == null) {

            events.remove(
                NowBarType.MUSIC
            )

        } else {

            events[NowBarType.MUSIC] =
                NowBarEvent(
                    item = item,
                    priority = PRIORITY_MUSIC
                )
        }

        render()
    }

    fun setNotification(
        item: NowBarItem?
    ) {

        if (item == null) {

            events.remove(
                NowBarType.NOTIFICATION
            )

        } else {

            events[NowBarType.NOTIFICATION] =
                NowBarEvent(
                    item = item,
                    priority = PRIORITY_NOTIFICATION
                )
        }

        render()
    }

    fun setDownload(
        item: NowBarItem?
    ) {

        if (item == null) {

            events.remove(
                NowBarType.DOWNLOAD
            )

        } else {

            events[NowBarType.DOWNLOAD] =
                NowBarEvent(
                    item = item,
                    priority = PRIORITY_DOWNLOAD
                )
        }

        render()
    }

    fun setCall(
        item: NowBarItem?
    ) {

        if (item == null) {

            events.remove(
                NowBarType.CALL
            )

        } else {

            events[NowBarType.CALL] =
                NowBarEvent(
                    item = item,
                    priority = PRIORITY_CALL
                )
        }

        render()
    }

    fun setNavigation(
        item: NowBarItem?
    ) {

        if (item == null) {

            events.remove(
                NowBarType.NAVIGATION
            )

        } else {

            events[NowBarType.NAVIGATION] =
                NowBarEvent(
                    item = item,
                    priority = PRIORITY_NAVIGATION
                )
        }

        render()
    }

    fun current(): NowBarItem? {

        val now =
            System.currentTimeMillis()

        events.entries.removeIf {
            val expires =
                it.value.expiresAt

            expires != null &&
                expires <= now
        }

        return events.values
            .maxWithOrNull(
                compareBy<NowBarEvent> {
                    it.priority
                }.thenBy {
                    it.item.isPersistent
                }
            )
            ?.item
    }

    private fun render() {

        val item =
            current()

        controller.setPrimary(
            item
        )
    }

    private fun updateBattery(
        intent: Intent
    ) {

        val level =
            intent.getIntExtra(
                BatteryManager.EXTRA_LEVEL,
                -1
            )

        val scale =
            intent.getIntExtra(
                BatteryManager.EXTRA_SCALE,
                -1
            )

        if (level < 0 || scale <= 0) {
            return
        }

        val percent =
            ((level * 100f) / scale)
                .toInt()
                .coerceIn(0, 100)

        val status =
            intent.getIntExtra(
                BatteryManager.EXTRA_STATUS,
                -1
            )

        val charging =
            status ==
                BatteryManager.BATTERY_STATUS_CHARGING ||
                status ==
                BatteryManager.BATTERY_STATUS_FULL

        when {

            charging -> {

                publish(
                    NowBarItem(
                        type =
                            NowBarType.BATTERY,
                        title =
                            "Charging",
                        subtitle =
                            "$percent%",
                        progress =
                            percent,
                        isPersistent =
                            false
                    ),
                    PRIORITY_BATTERY
                )
            }

            percent <= 15 -> {

                publish(
                    NowBarItem(
                        type =
                            NowBarType.BATTERY,
                        title =
                            "Low battery",
                        subtitle =
                            "$percent% remaining",
                        progress =
                            percent,
                        isPersistent =
                            false
                    ),
                    PRIORITY_LOW_BATTERY
                )
            }

            else -> {

                clear(
                    NowBarType.BATTERY
                )
            }
        }
    }

    companion object {

        // Reordered so CALL is the highest priority — an incoming/
        // active call should always outrank music or a running timer.
        const val PRIORITY_BATTERY = 10
        const val PRIORITY_LOW_BATTERY = 20
        const val PRIORITY_NOTIFICATION = 30
        const val PRIORITY_DOWNLOAD = 40
        const val PRIORITY_NAVIGATION = 50
        const val PRIORITY_MUSIC = 60
        const val PRIORITY_TIMER = 70
        const val PRIORITY_CALL = 90
    }
}
