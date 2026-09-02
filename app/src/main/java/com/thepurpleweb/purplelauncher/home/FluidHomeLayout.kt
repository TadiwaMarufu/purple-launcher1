package com.thepurpleweb.purplelauncher.home

import android.view.Gravity
import android.view.ViewGroup
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.TextView
import com.thepurpleweb.purplelauncher.apps.AppInfo
import com.thepurpleweb.purplelauncher.apps.HomeAppAdapter
import com.thepurpleweb.purplelauncher.performance.VisualQuality

class FluidHomeLayout : HomeLayout {

    override fun build(
        container: ViewGroup,
        apps: List<AppInfo>,
        quality: VisualQuality,
        reducedMotion: Boolean,
        onAppClick: (AppInfo) -> Unit
    ) {
        container.removeAllViews()

        val context = container.context
        val visuals = ProfileVisualsProvider.forMotion(MotionStyle.FLUID)

        container.setBackgroundColor(visuals.background)

        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(
                ProfileVisualsProvider.dp(this, visuals.horizontalPaddingDp),
                ProfileVisualsProvider.dp(this, 12),
                ProfileVisualsProvider.dp(this, visuals.horizontalPaddingDp),
                0
            )
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val title = TextView(context).apply {
            text = "Flow"
            textSize = visuals.titleSizeSp
            setTextColor(visuals.primaryText)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, ProfileVisualsProvider.dp(this, 6))
        }

        val subtitle = TextView(context).apply {
            text = "Everything where it naturally belongs."
            textSize = visuals.bodySizeSp
            setTextColor(visuals.secondaryText)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, ProfileVisualsProvider.dp(this, 18))
        }

        val grid = GridView(context).apply {
            numColumns = 4
            verticalSpacing = ProfileVisualsProvider.dp(this, 18)
            horizontalSpacing = ProfileVisualsProvider.dp(this, 6)
            stretchMode = GridView.STRETCH_COLUMN_WIDTH
            gravity = Gravity.CENTER
            clipToPadding = false
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            adapter = HomeAppAdapter(context, apps, onAppClick)
        }

        root.addView(title)
        root.addView(subtitle)
        root.addView(grid)

        container.addView(root)

        ProfileVisualsProvider.animate(root, MotionStyle.FLUID, quality, reducedMotion)
        ProfileVisualsProvider.animateChildren(grid, MotionStyle.FLUID, quality, reducedMotion)
    }
}
