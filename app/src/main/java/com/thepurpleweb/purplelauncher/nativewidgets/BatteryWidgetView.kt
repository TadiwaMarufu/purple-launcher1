package com.thepurpleweb.purplelauncher.nativewidgets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.BatteryManager
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat

class BatteryWidgetView(context: Context) : NativeWidgetView(context) {

    private var isRegistered = false

    private val percentText = TextView(context).apply {
        textSize = 32f
        setTextColor(Color.WHITE)
        gravity = Gravity.CENTER
    }

    private val statusText = TextView(context).apply {
        textSize = 13f
        setTextColor(Color.rgb(180, 180, 180))
        gravity = Gravity.CENTER
    }

    private val progressBar = ProgressBar(
        context, null, android.R.attr.progressBarStyleHorizontal
    ).apply {
        max = 100
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            if (intent == null) return
            updateFromIntent(intent)
        }
    }

    init {
        val column = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 16, 24, 16)
        }
        column.addView(percentText)
        column.addView(
            progressBar,
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 16).apply {
                topMargin = 12
                bottomMargin = 8
            }
        )
        column.addView(statusText)
        addView(column, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    private fun updateFromIntent(intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level < 0 || scale <= 0) return

        val percent = ((level * 100f) / scale).toInt().coerceIn(0, 100)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val charging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL

        percentText.text = "$percent%"
        progressBar.progress = percent
        statusText.text = if (charging) "Charging" else "On battery"
    }

    override fun start() {
        if (isRegistered) return
        try {
            // RECEIVER_NOT_EXPORTED per the same Android 13+ requirement
            // that caused the NowBarNotificationBridge crash risk earlier
            // this session — applying it defensively here too.
            ContextCompat.registerReceiver(
                context,
                receiver,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            isRegistered = true
        } catch (_: Exception) {
        }
    }

    override fun stop() {
        if (!isRegistered) return
        try {
            context.unregisterReceiver(receiver)
        } catch (_: Exception) {
        }
        isRegistered = false
    }
}
