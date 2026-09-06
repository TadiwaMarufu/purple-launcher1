package com.thepurpleweb.purplelauncher.canvas

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.thepurpleweb.purplelauncher.home.ProfileVisualsProvider
import com.thepurpleweb.purplelauncher.nativewidgets.NativeWidgetView
import com.thepurpleweb.purplelauncher.nowbar.NowBarMediaManager

class CanvasMediaModuleView(context: Context) : NativeWidgetView(context) {

    private var mediaManager: NowBarMediaManager? = null
    private var isCurrentlyPlaying = false

    private val titleText = TextView(context).apply {
        textSize = 15f
        setTextColor(CanvasModuleStyle.primaryText)
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        maxLines = 1
    }

    private val subtitleText = TextView(context).apply {
        textSize = 12f
        setTextColor(CanvasModuleStyle.secondaryText)
        maxLines = 1
    }

    private val playPauseButton = TextView(context).apply {
        text = "\u25B6"
        textSize = 18f
        setTextColor(CanvasModuleStyle.primaryText)
        gravity = Gravity.CENTER
        setPadding(dp(12), dp(6), dp(12), dp(6))
    }

    init {
        ProfileVisualsProvider.roundedBackground(
            this,
            CanvasModuleStyle.cardBackground,
            CanvasModuleStyle.cornerRadiusDp
        )

        val root = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(10), dp(12), dp(10))
        }

        val textColumn = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        textColumn.addView(titleText)
        textColumn.addView(subtitleText)

        val prevButton = transportGlyph("\u23EE")
        val nextButton = transportGlyph("\u23ED")

        prevButton.setOnClickListener { mediaManager?.skipPrevious() }
        nextButton.setOnClickListener { mediaManager?.skipNext() }
        playPauseButton.setOnClickListener {
            mediaManager?.togglePlayPause()
        }

        root.addView(textColumn)
        root.addView(prevButton)
        root.addView(playPauseButton)
        root.addView(nextButton)

        addView(root, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        showIdle()
    }

    private fun transportGlyph(symbol: String): TextView {
        return TextView(context).apply {
            text = symbol
            textSize = 15f
            setTextColor(CanvasModuleStyle.secondaryText)
            gravity = Gravity.CENTER
            setPadding(dp(8), dp(6), dp(8), dp(6))
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun showIdle() {
        isCurrentlyPlaying = false
        titleText.text = "Not playing"
        subtitleText.text = "Media will appear here"
        playPauseButton.text = "\u25B6"
    }

    override fun start() {
        if (mediaManager != null) return
        mediaManager = NowBarMediaManager(context) { item ->
            if (item == null) {
                showIdle()
            } else {
                isCurrentlyPlaying = true
                titleText.text = item.title
                subtitleText.text = item.subtitle ?: ""
                playPauseButton.text = "\u23F8"
            }
        }
        mediaManager?.start()
    }

    override fun stop() {
        mediaManager?.stop()
        mediaManager = null
    }
}
