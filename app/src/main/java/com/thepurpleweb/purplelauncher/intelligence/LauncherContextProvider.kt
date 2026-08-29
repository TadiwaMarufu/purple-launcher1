package com.thepurpleweb.purplelauncher.intelligence

import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import java.util.Calendar

/**
 * Collects inexpensive local device signals.
 *
 * This class intentionally avoids expensive background work.
 */
class LauncherContextProvider(
    private val context: Context
) {

    fun snapshot(): LauncherContext {

        val calendar =
            Calendar.getInstance()

        val batteryIntent =
            context.registerReceiver(
                null,
                android.content.IntentFilter(
                    Intent.ACTION_BATTERY_CHANGED
                )
            )

        val level =
            batteryIntent?.getIntExtra(
                BatteryManager.EXTRA_LEVEL,
                100
            ) ?: 100

        val scale =
            batteryIntent?.getIntExtra(
                BatteryManager.EXTRA_SCALE,
                100
            ) ?: 100

        val batteryPercent =
            if (scale > 0) {
                ((level * 100f) / scale)
                    .toInt()
                    .coerceIn(0, 100)
            } else {
                100
            }

        val status =
            batteryIntent?.getIntExtra(
                BatteryManager.EXTRA_STATUS,
                -1
            ) ?: -1

        val charging =
            status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        return LauncherContext(
            hourOfDay = calendar.get(Calendar.HOUR_OF_DAY),
            batteryPercent = batteryPercent,
            isCharging = charging
        )
    }
}
