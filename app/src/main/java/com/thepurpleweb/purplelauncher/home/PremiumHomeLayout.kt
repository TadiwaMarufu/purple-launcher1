package com.thepurpleweb.purplelauncher.home

import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.TextView
import com.thepurpleweb.purplelauncher.apps.AppInfo
import com.thepurpleweb.purplelauncher.apps.HomeAppAdapter
import com.thepurpleweb.purplelauncher.performance.VisualQuality

class PremiumHomeLayout : HomeLayout {

    override fun build(
        container: ViewGroup,
        apps: List<AppInfo>,
        quality: VisualQuality,
        reducedMotion: Boolean,
        onAppClick: (AppInfo) -> Unit
    ) {
        container.removeAllViews()

        val context = container.context
        val visuals = ProfileVisualsProvider.forMotion(MotionStyle.PREMIUM)
        val density = context.resources.displayMetrics.density

        container.setBackgroundColor(
            ProfileVisualsProvider.withAlpha(
                visuals.background,
                ProfileVisualsProvider.scrimAlphaFor(MotionStyle.PREMIUM)
            )
        )

        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                (visuals.horizontalPaddingDp * density).toInt(),
                (16 * density).toInt(),
                (visuals.horizontalPaddingDp * density).toInt(),
                0
            )
        }

        val header = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(
                (18 * density).toInt(),
                (16 * density).toInt(),
                (18 * density).toInt(),
                (16 * density).toInt()
            )

            ProfileVisualsProvider.roundedBackground(
                this,
                visuals.card,
                visuals.cornerRadiusDp
            )
        }

        val title = TextView(context).apply {
            text = "Premium"
            textSize = visuals.titleSizeSp
            setTextColor(visuals.primaryText)
        }

        val subtitle = TextView(context).apply {
            text = "Refined by design."
            textSize = visuals.bodySizeSp
            setTextColor(visuals.secondaryText)
            setPadding(0, (4 * density).toInt(), 0, 0)
        }

        header.addView(title)
        header.addView(subtitle)

        val grid = GridView(context).apply {
            numColumns = 4
            verticalSpacing = (16 * density).toInt()
            horizontalSpacing = (4 * density).toInt()
            stretchMode = GridView.STRETCH_COLUMN_WIDTH
            gravity = Gravity.CENTER
            clipToPadding = false
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = (18 * density).toInt()
            }
            adapter = HomeAppAdapter(context, apps, onAppClick)
        }

        root.addView(
            header,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
        root.addView(grid)

        (container as FrameLayout).addView(
            root,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL
            )
        )

        ProfileVisualsProvider.animate(root, MotionStyle.PREMIUM, quality, reducedMotion)
        ProfileVisualsProvider.animateChildren(grid, MotionStyle.PREMIUM, quality, reducedMotion)
    }
}
