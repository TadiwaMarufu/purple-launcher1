package com.thepurpleweb.purplelauncher.nativewidgets

import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.text.format.DateFormat
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.thepurpleweb.purplelauncher.canvas.CanvasModuleStyle
import com.thepurpleweb.purplelauncher.home.ProfileVisualsProvider
import java.util.Calendar

class ClockWidgetView(context: Context) : NativeWidgetView(context) {

    private val handler = Handler(Looper.getMainLooper())
    private var running = false

    private val timeText = TextView(context).apply {
        textSize = 52f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        setTextColor(CanvasModuleStyle.primaryText)
        gravity = Gravity.CENTER
    }

    private val divider = View(context).apply {
        setBackgroundColor(CanvasModuleStyle.accent)
    }

    private val dateText = TextView(context).apply {
        textSize = 13f
        setTextColor(CanvasModuleStyle.secondaryText)
        gravity = Gravity.CENTER
        letterSpacing = 0.04f
    }

    private val tick = object : Runnable {
        override fun run() {
            if (!running) return
            updateTime()
            handler.postDelayed(this, 1000L)
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
        column.addView(timeText)
        column.addView(
            divider,
            LinearLayout.LayoutParams(dp(28), dp(2)).apply {
                topMargin = dp(6)
                bottomMargin = dp(8)
            }
        )
        column.addView(dateText)

        addView(column, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        updateTime()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun updateTime() {
        val now = Calendar.getInstance()
        timeText.text = DateFormat.format("h:mm", now).toString()
        dateText.text = DateFormat.format("EEEE, MMMM d", now).toString().uppercase()
    }

    override fun start() {
        if (running) return
        running = true
        handler.post(tick)
    }

    override fun stop() {
        running = false
        handler.removeCallbacks(tick)
    }
}
