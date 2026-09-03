package com.thepurpleweb.purplelauncher.home

import android.view.Gravity
import android.view.ViewGroup
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

        container.setBackgroundColor(ProfileVisualsProvider.withAlpha(visuals.background, ProfileVisualsProvider.scrimAlphaFor(MotionStyle.CALM)))

        val gridView = GridView(context).apply {
            numColumns = 4
            verticalSpacing = ProfileVisualsProvider.dp(this, 16)
            horizontalSpacing = ProfileVisualsProvider.dp(this, 4)
            gravity = Gravity.CENTER
            clipToPadding = false
            setWillNotDraw(false)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            adapter = HomeAppAdapter(context, apps, onAppClick)
        }

        container.addView(gridView)

        ProfileVisualsProvider.animate(gridView, MotionStyle.CALM, quality, reducedMotion)
        ProfileVisualsProvider.animateChildren(gridView, MotionStyle.CALM, quality, reducedMotion)
    }
}
