package com.thepurpleweb.purplelauncher.canvas

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.widget.TextView
import com.thepurpleweb.purplelauncher.home.ProfileVisualsProvider
import com.thepurpleweb.purplelauncher.nativewidgets.NativeWidgetView

class CanvasQuoteModuleView(context: Context) : NativeWidgetView(context) {

    private val quotes = listOf(
        "Act boldly. Small steps every day lead to big results.",
        "Make Android fun again.",
        "Simplicity is the ultimate sophistication.",
        "Your phone, your frequency."
    )

    private val quoteText = TextView(context).apply {
        textSize = 15f
        setTextColor(CanvasModuleStyle.primaryText)
        typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
        gravity = Gravity.CENTER
        setPadding(dp(20), dp(14), dp(20), dp(14))
        setLineSpacing(dp(2).toFloat(), 1.1f)
    }

    init {
        ProfileVisualsProvider.roundedBackground(
            this,
            CanvasModuleStyle.cardBackground,
            CanvasModuleStyle.cornerRadiusDp
        )

        quoteText.text = "\u201C${quotes.random()}\u201D"
        addView(quoteText, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    override fun start() {}
    override fun stop() {}
}
