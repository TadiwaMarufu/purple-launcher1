package com.thepurpleweb.purplelauncher.nativewidgets

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.format.DateFormat
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import java.util.Calendar

class ClockWidgetView(context: Context) : NativeWidgetView(context) {

    private val handler = Handler(Looper.getMainLooper())
    private var running = false

    private val timeText = TextView(context).apply {
        textSize = 40f
        setTextColor(Color.WHITE)
        gravity = Gravity.CENTER
    }

    private val dateText = TextView(context).apply {
        textSize = 14f
        setTextColor(Color.rgb(180, 180, 180))
        gravity = Gravity.CENTER
    }

    private val tick = object : Runnable {
        override fun run() {
            if (!running) return
            updateTime()
            handler.postDelayed(this, 1000L)
        }
    }

    init {
        val column = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }
        column.addView(timeText)
        column.addView(dateText)
        addView(column, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        updateTime()
    }

    private fun updateTime() {
        val now = Calendar.getInstance()
        timeText.text = DateFormat.format("h:mm a", now).toString()
        dateText.text = DateFormat.format("EEEE, MMMM d", now).toString()
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
