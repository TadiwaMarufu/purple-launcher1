package com.thepurpleweb.purplelauncher.home

import android.view.Gravity
import android.view.ViewGroup
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.TextView
import com.thepurpleweb.purplelauncher.apps.AppInfo
import com.thepurpleweb.purplelauncher.apps.HomeAppAdapter

class PremiumHomeLayout : HomeLayout {

    override fun build(
        container: ViewGroup,
        apps: List<AppInfo>,
        onAppClick: (AppInfo) -> Unit
    ) {
        container.removeAllViews()

        val context = container.context
        val visuals = ProfileVisualsProvider.forMotion(MotionStyle.PREMIUM)

        container.setBackgroundColor(visuals.background)

        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                ProfileVisualsProvider.dp(this, visuals.horizontalPaddingDp),
                ProfileVisualsProvider.dp(this, 16),
                ProfileVisualsProvider.dp(this, visuals.horizontalPaddingDp),
                0
            )
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val header = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(
                ProfileVisualsProvider.dp(this, 18),
                ProfileVisualsProvider.dp(this, 16),
                ProfileVisualsProvider.dp(this, 18),
                ProfileVisualsProvider.dp(this, 16)
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
            setPadding(
                0,
                ProfileVisualsProvider.dp(this, 4),
                0,
                0
            )
        }

        header.addView(title)
        header.addView(subtitle)

        val grid = GridView(context).apply {
            numColumns = 4
            verticalSpacing = ProfileVisualsProvider.dp(this, 16)
            horizontalSpacing = ProfileVisualsProvider.dp(this, 4)
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

        val gridParams = grid.layoutParams as LinearLayout.LayoutParams
        gridParams.topMargin = ProfileVisualsProvider.dp(grid, 18)
        grid.layoutParams = gridParams

        root.addView(
            header,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(grid)

        container.addView(root)

        ProfileVisualsProvider.animate(root, MotionStyle.PREMIUM)
        ProfileVisualsProvider.animateChildren(grid, MotionStyle.PREMIUM)
    }
}
