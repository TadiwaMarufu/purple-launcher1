package com.thepurpleweb.purplelauncher.nowbar

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.thepurpleweb.purplelauncher.motion.MotionEngine
import com.thepurpleweb.purplelauncher.motion.ProfileMotion
import com.thepurpleweb.purplelauncher.performance.VisualQuality
import com.thepurpleweb.purplelauncher.profile.Profile
import com.thepurpleweb.purplelauncher.profile.ProfileDesign

class NowBarView(
    context: Context
) : LinearLayout(context) {

    private val titleView =
        TextView(context)

    private val subtitleView =
        TextView(context)

    private val timeView =
        TextView(context)

    private val primaryColumn =
        LinearLayout(context)

    private val actionRow =
        LinearLayout(context)

    private var currentProfile =
        Profile.Calm

    private var expanded =
        false

    private var onSwitchProfile:
        (() -> Unit)? = null

    private var onSearch:
        (() -> Unit)? = null

    private var onNotifications:
        (() -> Unit)? = null

    init {

        orientation =
            VERTICAL

        gravity =
            Gravity.CENTER_VERTICAL

        setPadding(
            dp(16),
            dp(10),
            dp(16),
            dp(10)
        )

        minimumHeight =
            dp(64)

        isClickable = true
        isFocusable = true

        buildCollapsedContent()

        setOnClickListener {
            toggleExpanded()
        }
    }

    fun setActions(
        onSwitchProfile: () -> Unit,
        onSearch: () -> Unit,
        onNotifications: () -> Unit
    ) {
        this.onSwitchProfile =
            onSwitchProfile

        this.onSearch =
            onSearch

        this.onNotifications =
            onNotifications
    }

    fun setProfile(
        profile: Profile,
        quality: VisualQuality
    ) {
        currentProfile =
            profile

        applyProfileVisuals(
            profile
        )

        updateMotion(
            profile,
            quality
        )

        updateContent()
    }

    fun setState(
        state: NowBarState,
        quality: VisualQuality
    ) {
        expanded =
            state.expanded

        applyProfileVisuals(
            currentProfile
        )

        buildContent(
            state
        )

        updateMotion(
            currentProfile,
            quality
        )
    }

    private fun buildCollapsedContent() {

        removeAllViews()

        primaryColumn =
            LinearLayout(context).apply {

                orientation =
                    HORIZONTAL

                gravity =
                    Gravity.CENTER_VERTICAL
            }

        val textColumn =
            LinearLayout(context).apply {

                orientation =
                    VERTICAL

                gravity =
                    Gravity.CENTER_VERTICAL

                layoutParams =
                    LayoutParams(
                        0,
                        LayoutParams.WRAP_CONTENT,
                        1f
                    )
            }

        titleView.apply {

            textSize = 14f

            maxLines = 1
        }

        subtitleView.apply {

            textSize = 11f

            maxLines = 1
        }

        timeView.apply {

            textSize = 15f

            gravity =
                Gravity.CENTER
        }

        textColumn.addView(
            titleView
        )

        textColumn.addView(
            subtitleView
        )

        primaryColumn.addView(
            textColumn
        )

        primaryColumn.addView(
            timeView,
            LayoutParams(
                dp(58),
                LayoutParams.WRAP_CONTENT
            )
        )

        addView(
            primaryColumn
        )
    }

    private fun buildContent(
        state: NowBarState
    ) {

        removeAllViews()

        if (!expanded) {

            buildCollapsedContent()

            return
        }

        val topRow =
            LinearLayout(context).apply {

                orientation =
                    HORIZONTAL

                gravity =
                    Gravity.CENTER_VERTICAL
            }

        val titleColumn =
            LinearLayout(context).apply {

                orientation =
                    VERTICAL

                layoutParams =
                    LayoutParams(
                        0,
                        LayoutParams.WRAP_CONTENT,
                        1f
                    )
            }

        titleView.apply {
            textSize = 16f
        }

        subtitleView.apply {
            textSize = 12f
        }

        titleColumn.addView(
            titleView
        )

        titleColumn.addView(
            subtitleView
        )

        timeView.apply {
            textSize = 16f
        }

        topRow.addView(
            titleColumn
        )

        topRow.addView(
            timeView
        )

        addView(
            topRow
        )

        if (state.primary != null) {

            addNowItem(
                state.primary
            )
        }

        if (state.secondary != null) {

            addNowItem(
                state.secondary
            )
        }

        buildActionRow()
    }

    private fun addNowItem(
        item: NowBarItem
    ) {

        val row =
            TextView(context).apply {

                text =
                    if (item.subtitle.isNullOrBlank()) {
                        item.title
                    } else {
                        "${item.title}  •  ${item.subtitle}"
                    }

                textSize = 13f

                setPadding(
                    0,
                    dp(8),
                    0,
                    dp(4)
                )

                maxLines = 2
            }

        addView(
            row
        )
    }

    private fun buildActionRow() {

        actionRow =
            LinearLayout(context).apply {

                orientation =
                    HORIZONTAL

                gravity =
                    Gravity.CENTER

                setPadding(
                    0,
                    dp(8),
                    0,
                    0
                )
            }

        addAction(
            "Profile"
        ) {
            onSwitchProfile?.invoke()
        }

        addAction(
            "Search"
        ) {
            onSearch?.invoke()
        }

        addAction(
            "Notifications"
        ) {
            onNotifications?.invoke()
        }

        addView(
            actionRow
        )
    }

    private fun addAction(
        label: String,
        action: () -> Unit
    ) {

        val button =
            TextView(context).apply {

                text = label

                textSize = 11f

                gravity =
                    Gravity.CENTER

                setPadding(
                    dp(10),
                    dp(7),
                    dp(10),
                    dp(7)
                )

                isClickable = true

                setOnClickListener {
                    action()
                }
            }

        actionRow.addView(
            button,
            LayoutParams(
                0,
                LayoutParams.WRAP_CONTENT,
                1f
            )
        )
    }

    private fun updateContent() {

        val hour =
            java.util.Calendar
                .getInstance()
                .get(java.util.Calendar.HOUR_OF_DAY)

        val minute =
            java.util.Calendar
                .getInstance()
                .get(java.util.Calendar.MINUTE)

        val time =
            String.format(
                "%02d:%02d",
                hour,
                minute
            )

        timeView.text =
            time

        titleView.text =
            currentProfile.displayName

        subtitleView.text =
            when (currentProfile) {

                Profile.Fluid ->
                    "Living mode"

                Profile.Premium ->
                    "Refined mode"

                Profile.Calm ->
                    "Quiet mode"

                Profile.Focus ->
                    "Focus mode"

                Profile.Expressive ->
                    "Creative mode"
            }
    }

    private fun applyProfileVisuals(
        profile: Profile
    ) {

        val design =
            ProfileDesign.forProfile(
                profile
            )

        val background =
            when (profile) {

                Profile.Fluid ->
                    Color.rgb(31, 22, 43)

                Profile.Premium ->
                    Color.rgb(27, 26, 31)

                Profile.Calm ->
                    Color.rgb(25, 25, 28)

                Profile.Focus ->
                    Color.rgb(23, 26, 33)

                Profile.Expressive ->
                    Color.rgb(39, 19, 48)
            }

        val primary =
            when (profile) {

                Profile.Fluid ->
                    Color.rgb(248, 244, 255)

                Profile.Premium ->
                    Color.rgb(245, 243, 248)

                Profile.Calm ->
                    Color.rgb(238, 238, 242)

                Profile.Focus ->
                    Color.rgb(244, 246, 250)

                Profile.Expressive ->
                    Color.rgb(255, 246, 255)
            }

        val secondary =
            when (profile) {

                Profile.Fluid ->
                    Color.rgb(180, 166, 196)

                Profile.Premium ->
                    Color.rgb(163, 160, 170)

                Profile.Calm ->
                    Color.rgb(145, 145, 152)

                Profile.Focus ->
                    Color.rgb(145, 151, 163)

                Profile.Expressive ->
                    Color.rgb(190, 158, 201)
            }

        val accent =
            when (profile) {

                Profile.Fluid ->
                    Color.rgb(190, 105, 255)

                Profile.Premium ->
                    Color.rgb(205, 177, 255)

                Profile.Calm ->
                    Color.rgb(170, 125, 210)

                Profile.Focus ->
                    Color.rgb(169, 116, 255)

                Profile.Expressive ->
                    Color.rgb(221, 91, 255)
            }

        background =
            background

        titleView.setTextColor(
            primary
        )

        subtitleView.setTextColor(
            secondary
        )

        timeView.setTextColor(
            accent
        )

        for (i in 0 until actionRow.childCount) {

            actionRow
                .getChildAt(i)
                .let { child ->

                    if (child is TextView) {
                        child.setTextColor(
                            accent
                        )
                    }
                }
        }

        val radius =
            design.cornerRadiusDp
                .toFloat()
                .coerceAtLeast(12f)

        background =
            background

        this.background =
            GradientDrawable().apply {

                setColor(
                    background
                )

                cornerRadius =
                    radius *
                        resources
                            .displayMetrics
                            .density
            }
    }

    private fun updateMotion(
        profile: Profile,
        quality: VisualQuality
    ) {

        val motion =
            ProfileMotion.forProfile(
                profile
            )

        MotionEngine.animateEntrance(
            this,
            motion,
            quality
        )
    }

    private fun toggleExpanded() {

        expanded =
            !expanded

        if (expanded) {

            buildContent(
                NowBarState(
                    primary =
                        NowBarItem(
                            type =
                                NowBarType.IDLE,
                            title =
                                currentProfile.displayName,
                            subtitle =
                                "Now Bar active"
                        ),
                    expanded = true
                )
            )

        } else {

            buildCollapsedContent()
        }

        applyProfileVisuals(
            currentProfile
        )

        updateContent()
    }

    private fun dp(value: Int): Int =
        (
            value *
                resources
                    .displayMetrics
                    .density
            ).toInt()
}
