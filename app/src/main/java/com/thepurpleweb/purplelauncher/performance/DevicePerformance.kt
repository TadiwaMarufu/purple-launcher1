package com.thepurpleweb.purplelauncher.performance

import android.app.ActivityManager
import android.content.Context
import android.os.Build

object DevicePerformance {

    fun classify(context: Context): VisualQuality {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val memoryClassMb = activityManager.memoryClass

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val lowRam = activityManager.isLowRamDevice

            if (lowRam || memoryClassMb <= 128) {
                return VisualQuality.LOW
            }
        }

        return when {
            memoryClassMb <= 192 -> VisualQuality.LOW
            memoryClassMb <= 384 -> VisualQuality.MEDIUM
            else -> VisualQuality.HIGH
        }
    }
}
