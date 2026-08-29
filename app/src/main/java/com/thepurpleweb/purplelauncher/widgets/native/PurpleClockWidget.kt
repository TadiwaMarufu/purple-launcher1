package com.thepurpleweb.purplelauncher.widgets.native

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.thepurpleweb.purplelauncher.motion.MotionEngine
import com.thepurpleweb.purplelauncher.motion.ProfileMotion
import com.thepurpleweb.purplelauncher.performance.VisualQuality
import com.thepurpleweb.purplelauncher.profile.ProfileDesign
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PurpleClockWidget : PurpleWidget {

    override val id: String = PurpleWidgetType.CLOCK.id

    private var clockView: TextView? = null
    private val handler = Handler(Looper.getMainLooper())

    private val updater = object : Runnable {
        override fun run() {
            updateTime()
            handler.postDelayed(this, 30_000L)
        }
    }

    override fun createView(
        parent: ViewGroup,
        design: ProfileDesign,
        quality: VisualQuality
    ): View {
        val context = parent.context

        val textView = TextView(context).apply {
            gravity = Gravity.CENTER_VERTICAL
            textSize = design.titleSizeSp
            setTextColor(Color.WHITE)
            setPadding(
                dp(design.horizontalPaddingDp),
                dp(design.verticalPaddingDp),
                dp(design.horizontalPaddingDp),
                dp(design.verticalPaddingDp)
            )

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            updateTime()
        }

        clockView = textView

        MotionEngine.animateEntrance(
            textView,
            ProfileMotion.forProfile(design.profile),
            quality
        )

        return textView
    }

    override fun onStart() {
        handler.removeCallbacks(updater)
        updateTime()
        handler.post(updater)
    }

    override fun onStop() {
        handler.removeCallbacks(updater)
    }

    private fun updateTime() {
        val text = SimpleDateFormat(
            "h:mm",
            Locale.getDefault()
        ).format(Date())

        clockView?.text = text
    }

    private fun dp(value: Int): Int =
        ((value * (clockView?.resources?.displayMetrics?.density ?: 1f))).toInt()
}
