package com.thepurpleweb.purplelauncher.nowbar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.thepurpleweb.purplelauncher.home.MotionStyle
import com.thepurpleweb.purplelauncher.home.ProfileVisualsProvider
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
    }

    private val compactHeader = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
    }

    private val accentDot = View(context).apply {
        layoutParams = LinearLayout.LayoutParams(dp(10), dp(10)).apply {
            marginEnd = dp(10)
        }
    }

    private val titleView = TextView(context).apply {
        setSingleLine()
    }

    private val subtitleView = TextView(context).apply {
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

    private val progressBar = ProgressBar(
        context, null, android.R.attr.progressBarStyleHorizontal
    ).apply {
        max = 100
        visibility = View.GONE
    }

    private val expandedDetailText = TextView(context).apply {
        textSize = 12f
        visibility = View.GONE
    }

    private var isExpanded = false
    private var currentItem: NowBarItem? = null
    private var currentProfile: Profile = Profile.Calm

    private var onSearchClick: (() -> Unit)? = null
    private var onProfileClick: (() -> Unit)? = null
    private var onNotificationsClick: (() -> Unit)? = null

    init {
        compactHeader.addView(accentDot)
        compactHeader.addView(
            textContainer,
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        )

        expandedContent.addView(
            progressBar,
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(4))
        )
        expandedContent.addView(
            expandedDetailText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(12)
            }
        )

        container.addView(compactHeader)
        container.addView(expandedContent)

        addView(container, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))

        setOnClickListener { toggleExpand() }

        applyVisuals(currentProfile)
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

    fun setProfile(profile: Profile, quality: VisualQuality, reducedMotion: Boolean = false) {
        currentProfile = profile
        applyVisuals(profile)
    }

    private fun motionStyleFor(profile: Profile): MotionStyle = when (profile) {
        Profile.Fluid -> MotionStyle.FLUID
        Profile.Premium -> MotionStyle.PREMIUM
        Profile.Calm -> MotionStyle.CALM
        Profile.Focus -> MotionStyle.FOCUS
        Profile.Expressive -> MotionStyle.EXPRESSIVE
    }

    private fun applyVisuals(profile: Profile) {
        val visuals = ProfileVisualsProvider.forMotion(motionStyleFor(profile))

        ProfileVisualsProvider.roundedBackground(container, visuals.card, visuals.cornerRadiusDp)

        titleView.setTextColor(visuals.primaryText)
        titleView.textSize = visuals.bodySizeSp + 2f

        subtitleView.setTextColor(visuals.secondaryText)
        subtitleView.textSize = visuals.bodySizeSp - 1f

        expandedDetailText.setTextColor(visuals.secondaryText)

        accentDot.background = ovalDrawable(visuals.accent)
        progressBar.progressTintList = ColorStateList.valueOf(visuals.accent)
    }

    private fun ovalDrawable(color: Int): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
        }
    }

    fun setState(item: NowBarItem?, quality: VisualQuality, reducedMotion: Boolean = false) {
        val previousKey = currentItem?.let { "${it.type}-${it.title}-${it.subtitle}" }
        currentItem = item

        if (item == null) {
            titleView.text = "Purple Launcher"
            subtitleView.text = "Idle"
            accentDot.alpha = 0.3f
            expandedContent.visibility = View.GONE
            return
        }

        accentDot.alpha = 1f
        titleView.text = item.title
        subtitleView.text = item.subtitle?.ifEmpty { item.type.name } ?: item.type.name

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

        // Only pulse when content actually changed (not on every tick of
        // an unrelated re-render), and only when quality/motion settings
        // allow it — this is the previously-unused ProfileVisualsProvider
        // .pulse() finally getting a caller.
        val newKey = "${item.type}-${item.title}-${item.subtitle}"
        if (newKey != previousKey && quality != VisualQuality.LOW && !reducedMotion) {
            ProfileVisualsProvider.pulse(container, motionStyleFor(currentProfile))
        }
    }

    private fun toggleExpand() {
        isExpanded = !isExpanded
        if (isExpanded && currentItem != null) {
            expandedDetailText.text = "Type: ${currentItem?.type?.name} • Persistent: ${currentItem?.isPersistent}"
            expandedDetailText.visibility = View.VISIBLE
            expandedContent.visibility = View.VISIBLE
        } else {
            expandedContent.visibility = View.GONE
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
