package com.thepurpleweb.purplelauncher.home

import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridView
import com.thepurpleweb.purplelauncher.apps.AppInfo
import com.thepurpleweb.purplelauncher.apps.HomeAppAdapter
import com.thepurpleweb.purplelauncher.performance.VisualQuality

class CalmHomeLayout : HomeLayout {

    override fun build(
        container: ViewGroup,
        apps: List<AppInfo>,
        quality: VisualQuality,
        reducedMotion: Boolean,
        onAppClick: (AppInfo) -> Unit
    ) {
        container.removeAllViews()

        val context = container.context
        val visuals = ProfileVisualsProvider.forMotion(MotionStyle.CALM)

        container.setBackgroundColor(
            ProfileVisualsProvider.withAlpha(
                visuals.background,
                ProfileVisualsProvider.scrimAlphaFor(MotionStyle.CALM)
            )
        )

        val gridView = GridView(context).apply {
            numColumns = 4
            verticalSpacing = ProfileVisualsProvider.dp(this, 16)
            horizontalSpacing = ProfileVisualsProvider.dp(this, 4)
            gravity = Gravity.CENTER
            clipToPadding = false
            setWillNotDraw(false)
            adapter = HomeAppAdapter(context, apps, onAppClick)
        }

        // WRAP_CONTENT + CENTER_VERTICAL instead of MATCH_PARENT: with
        // only a handful of curated apps, stretching to fill the whole
        // container left the grid pinned to the top and a large empty
        // scrim below it. Centering makes it look intentional regardless
        // of how many apps are actually curated.
        (container as FrameLayout).addView(
            gridView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL
            )
        )

        ProfileVisualsProvider.animate(gridView, MotionStyle.CALM, quality, reducedMotion)
        ProfileVisualsProvider.animateChildren(gridView, MotionStyle.CALM, quality, reducedMotion)
    }
}
