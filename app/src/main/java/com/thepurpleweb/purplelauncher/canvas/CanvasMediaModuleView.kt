package com.thepurpleweb.purplelauncher.canvas

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.thepurpleweb.purplelauncher.nativewidgets.NativeWidgetView
import com.thepurpleweb.purplelauncher.nowbar.NowBarMediaManager

class CanvasMediaModuleView(context: Context) : NativeWidgetView(context) {

    private var mediaManager: NowBarMediaManager? = null

    private val titleText = TextView(context).apply {
        textSize = 16f
        setTextColor(Color.WHITE)
        maxLines = 1
    }

    private val subtitleText = TextView(context).apply {
        textSize = 12f
        setTextColor(Color.rgb(170, 170, 170))
        maxLines = 1
    }

    init {
        val column = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 16, 24, 16)
        }
        column.addView(titleText)
        column.addView(subtitleText)
        addView(column, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        setBackgroundColor(Color.rgb(18, 18, 18))
        showIdle()
    }

    private fun showIdle() {
        titleText.text = "Not playing"
        subtitleText.text = "Media will appear here"
    }

    override fun start() {
        if (mediaManager != null) return
        mediaManager = NowBarMediaManager(context) { item ->
            if (item == null) {
                showIdle()
            } else {
                titleText.text = item.title
                subtitleText.text = item.subtitle ?: ""
            }
        }
        mediaManager?.start()
    }

    override fun stop() {
        mediaManager?.stop()
        mediaManager = null
    }
}
