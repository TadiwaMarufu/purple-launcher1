package com.thepurpleweb.purplelauncher.nowbar

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

    private var primaryColumn =
        LinearLayout(context)

    private var actionRow =
        LinearLayout(context)

    /*
     * Explicit Profile type is important here.
     *
     * Without it Kotlin infers Profile.Calm from the
     * initializer, which makes the other profiles appear
     * to be incompatible types.
     */
    private var currentProfile: Profile =
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

        updateContent()
    }

    private fun buildCollapsedContent() {

        removeAllViews()

        // Detach reusable views from any previous parent.
        (titleView.parent as? ViewGroup)?.removeView(titleView)
        (subtitleView.parent as? ViewGroup)?.removeView(subtitleView)
        (timeView.parent as? ViewGroup)?.removeView(timeView)

        primaryColumn =
            LinearLayout(context).apply {

                orientation =
                    HORIZONTAL

                gravity =
                    Gravity.CENTER_VERTICAL

                layoutParams =
                    LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                    )
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
            textSize =
                16f
        }

        subtitleView.apply {
            textSize =
                12f
        }

        titleColumn.addView(
            titleView
        )

        titleColumn.addView(
            subtitleView
        )

        timeView.apply {
            textSize =
                16f
        }

        topRow.addView(
            titleColumn
        )

        topRow.addView(
            timeView
        )

        addView(
            topRow,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
        )

        state.primary?.let {
            addNowItem(it)
        }

        state.secondary?.let {
            addNowItem(it)
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

                textSize =
                    13f

                setPadding(
                    0,
                    dp(8),
                    0,
                    dp(4)
                )

                maxLines =
                    2
            }

        addView(
            row,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
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
            actionRow,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
        )
    }

    private fun addAction(
        label: String,
        action: () -> Unit
    ) {

        val button =
            TextView(context).apply {

                text =
                    label

                textSize =
                    11f

                gravity =
                    Gravity.CENTER

                setPadding(
                    dp(10),
                    dp(7),
                    dp(10),
                    dp(7)
                )

                isClickable =
                    true

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

        val calendar =
            java.util.Calendar.getInstance()

        val hour =
            calendar.get(
                java.util.Calendar.HOUR_OF_DAY
            )

        val minute =
            calendar.get(
                java.util.Calendar.MINUTE
            )

        timeView.text =
            String.format(
                "%02d:%02d",
                hour,
                minute
            )

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

        val backgroundColor =
            when (profile) {

                Profile.Fluid ->
                    Color.rgb(
                        31,
                        22,
                        43
                    )

                Profile.Premium ->
                    Color.rgb(
                        27,
                        26,
                        31
                    )

                Profile.Calm ->
                    Color.rgb(
                        25,
                        25,
                        28
                    )

                Profile.Focus ->
                    Color.rgb(
                        23,
                        26,
                        33
                    )

                Profile.Expressive ->
                    Color.rgb(
                        39,
                        19,
                        48
                    )
            }

        val primaryColor =
            when (profile) {

                Profile.Fluid ->
                    Color.rgb(
                        248,
                        244,
                        255
                    )

                Profile.Premium ->
                    Color.rgb(
                        245,
                        243,
                        248
                    )

                Profile.Calm ->
                    Color.rgb(
                        238,
                        238,
                        242
                    )

                Profile.Focus ->
                    Color.rgb(
                        244,
                        246,
                        250
                    )

                Profile.Expressive ->
                    Color.rgb(
                        255,
                        246,
                        255
                    )
            }

        val secondaryColor =
            when (profile) {

                Profile.Fluid ->
                    Color.rgb(
                        180,
                        166,
                        196
                    )

                Profile.Premium ->
                    Color.rgb(
                        163,
                        160,
                        170
                    )

                Profile.Calm ->
                    Color.rgb(
                        145,
                        145,
                        152
                    )

                Profile.Focus ->
                    Color.rgb(
                        145,
                        151,
                        163
                    )

                Profile.Expressive ->
                    Color.rgb(
                        190,
                        158,
                        201
                    )
            }

        val accentColor =
            when (profile) {

                Profile.Fluid ->
                    Color.rgb(
                        190,
                        105,
                        255
                    )

                Profile.Premium ->
                    Color.rgb(
                        205,
                        177,
                        255
                    )

                Profile.Calm ->
                    Color.rgb(
                        170,
                        125,
                        210
                    )

                Profile.Focus ->
                    Color.rgb(
                        169,
                        116,
                        255
                    )

                Profile.Expressive ->
                    Color.rgb(
                        221,
                        91,
                        255
                    )
            }

        titleView.setTextColor(
            primaryColor
        )

        subtitleView.setTextColor(
            secondaryColor
        )

        timeView.setTextColor(
            accentColor
        )

        for (
            i in 0 until actionRow.childCount
        ) {

            val child =
                actionRow.getChildAt(i)

            if (child is TextView) {

                child.setTextColor(
                    accentColor
                )
            }
        }

        val radius =
            design.cornerRadiusDp
                .toFloat()
                .coerceAtLeast(12f)

        this.background =
            GradientDrawable().apply {

                setColor(
                    backgroundColor
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
                    expanded =
                        true
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

    private fun dp(
        value: Int
    ): Int =
        (
            value *
                resources
                    .displayMetrics
                    .density
            ).toInt()
}
