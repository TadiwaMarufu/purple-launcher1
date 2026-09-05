package com.thepurpleweb.purplelauncher.canvas

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.TextView
import com.thepurpleweb.purplelauncher.nativewidgets.NativeWidgetView

class CanvasQuoteModuleView(context: Context) : NativeWidgetView(context) {

    private val quotes = listOf(
        "Act boldly. Small steps every day lead to big results.",
        "Make Android fun again.",
        "Simplicity is the ultimate sophistication.",
        "Your phone, your frequency."
    )

    private val quoteText = TextView(context).apply {
        textSize = 14f
        setTextColor(Color.WHITE)
        gravity = Gravity.CENTER
        setPadding(24, 16, 24, 16)
    }

    init {
        quoteText.text = "\u201C${quotes.random()}\u201D"
        addView(quoteText, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        setBackgroundColor(Color.rgb(18, 18, 18))
    }

    override fun start() {}
    override fun stop() {}
}
