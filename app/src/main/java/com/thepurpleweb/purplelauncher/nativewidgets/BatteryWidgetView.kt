package com.thepurpleweb.purplelauncher.nativewidgets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.BatteryManager
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.thepurpleweb.purplelauncher.canvas.CanvasModuleStyle
import com.thepurpleweb.purplelauncher.home.ProfileVisualsProvider

class BatteryWidgetView(context: Context) : NativeWidgetView(context) {

    private var isRegistered = false

    private val ring = BatteryRingView(context)

    private val percentText = TextView(context).apply {
        textSize = 20f
        setTextColor(CanvasModuleStyle.primaryText)
        gravity = Gravity.CENTER
    }

    private val statusText = TextView(context).apply {
        textSize = 12f
        setTextColor(CanvasModuleStyle.secondaryText)
        gravity = Gravity.CENTER
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            if (intent == null) return
            updateFromIntent(intent)
        }
    }

    init {
        ProfileVisualsProvider.roundedBackground(
            this,
            CanvasModuleStyle.cardBackground,
            CanvasModuleStyle.cornerRadiusDp
        )

        val column = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(16), dp(12), dp(16), dp(12))
        }

        val ringSize = dp(64)
        val ringStack = FrameLayout(context)
        ringStack.addView(ring, FrameLayout.LayoutParams(ringSize, ringSize))
        ringStack.addView(
            percentText,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
        )

        column.addView(ringStack, LinearLayout.LayoutParams(ringSize, ringSize))
        column.addView(
            statusText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(8) }
        )

        addView(column, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun updateFromIntent(intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level < 0 || scale <= 0) return

        val percent = ((level * 100f) / scale).toInt().coerceIn(0, 100)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val charging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL

        percentText.text = "$percent%"
        ring.progress = percent
        ring.invalidate()
        statusText.text = if (charging) "Charging" else "On battery"
    }

    override fun start() {
        if (isRegistered) return
        try {
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

    /**
     * Small custom View for the ring itself — no Material Components
     * dependency exists in this project, so a hand-drawn arc is the
     * correct, real approach rather than pulling in a new library for
     * one shape.
     */
    private class BatteryRingView(context: Context) : View(context) {

        var progress: Int = 0

        private val strokeWidthPx = 8f * resources.displayMetrics.density

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = strokeWidthPx
            strokeCap = Paint.Cap.ROUND
        }

        private val rect = RectF()

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val inset = strokeWidthPx / 2f
            rect.set(inset, inset, width - inset, height - inset)

            paint.color = CanvasModuleStyle.trackColor
            canvas.drawArc(rect, 0f, 360f, false, paint)

            paint.color = CanvasModuleStyle.accent
            val sweep = 360f * (progress / 100f)
            canvas.drawArc(rect, -90f, sweep, false, paint)
        }
    }
}
