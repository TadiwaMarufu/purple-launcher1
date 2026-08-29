package com.thepurpleweb.purplelauncher.nowbar

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.thepurpleweb.purplelauncher.performance.VisualQuality
import com.thepurpleweb.purplelauncher.profile.Profile

class NowBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val container = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(32, 24, 32, 24)
        setBackgroundColor(Color.parseColor("#1E1E2C"))
    }

    private val compactHeader = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
    }

    private val titleView = TextView(context).apply {
        setTextColor(Color.WHITE)
        textSize = 15f
        setSingleLine()
    }

    private val subtitleView = TextView(context).apply {
        setTextColor(Color.LTGRAY)
        textSize = 13f
        setSingleLine()
    }

    private val textContainer = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        addView(titleView)
        addView(subtitleView)
    }

    private val expandedContent = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        visibility = View.GONE
        setPadding(0, 16, 0, 0)
    }

    private val progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal).apply {
        max = 100
        visibility = View.GONE
    }

    private val expandedDetailText = TextView(context).apply {
        setTextColor(Color.GRAY)
        textSize = 12f
        visibility = View.GONE
    }

    private var isExpanded = false
    private var onSearchClick: (() -> Unit)? = null
    private var onProfileClick: (() -> Unit)? = null
    private var onNotificationsClick: (() -> Unit)? = null

    init {
        compactHeader.addView(
            textContainer,
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        )

        expandedContent.addView(
            progressBar,
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 12)
        )
        expandedContent.addView(
            expandedDetailText,
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = 12
            }
        )

        container.addView(compactHeader)
        container.addView(expandedContent)

        addView(
            container,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        )

        setOnClickListener {
            toggleExpand()
        }
    }

    fun setActions(
        onSwitchProfile: () -> Unit,
        onSearch: () -> Unit,
        onNotifications: () -> Unit
    ) {
        this.onProfileClick = onSwitchProfile
        this.onSearchClick = onSearch
        this.onNotificationsClick = onNotifications
    }

    fun setProfile(profile: Profile, quality: VisualQuality) {
        val colorHex = when (profile) {
            Profile.Fluid -> "#2A2A3D"
            Profile.Premium -> "#1F1F1F"
            Profile.Calm -> "#1D2B2A"
            Profile.Focus -> "#1A2433"
            Profile.Expressive -> "#331A2E"
        }
        container.setBackgroundColor(Color.parseColor(colorHex))
    }

    fun setState(item: NowBarItem?, quality: VisualQuality) {
        if (item == null) {
            titleView.text = "Purple Launcher"
            subtitleView.text = "Idle"
            expandedContent.visibility = View.GONE
            return
        }

        titleView.text = item.title
        subtitleView.text = item.subtitle.ifEmpty { item.type.name }

        if (item.progress != null && item.progress >= 0) {
            progressBar.progress = item.progress
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }

        if (isExpanded) {
            expandedDetailText.text = "Type: ${item.type.name} • Persistent: ${item.isPersistent}"
            expandedDetailText.visibility = View.VISIBLE
            expandedContent.visibility = View.VISIBLE
        } else {
            expandedContent.visibility = View.GONE
        }
    }

    private fun toggleExpand() {
        isExpanded = !isExpanded
        expandedContent.visibility = if (isExpanded) View.VISIBLE else View.GONE
    }
}
