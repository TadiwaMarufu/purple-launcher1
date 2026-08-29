package com.thepurpleweb.purplelauncher.home

import android.view.Gravity
import android.view.ViewGroup
import android.widget.GridView
import com.thepurpleweb.purplelauncher.apps.AppInfo
import com.thepurpleweb.purplelauncher.apps.HomeAppAdapter

/**
 * Calm: minimal information surface. A quiet curated grid,
 * generous whitespace, no extra chrome.
 */
class CalmHomeLayout : HomeLayout {

    override fun build(
        container: ViewGroup,
        apps: List<AppInfo>,
        onAppClick: (AppInfo) -> Unit
    ) {
        container.removeAllViews()

        val context = container.context

        val gridView = GridView(context).apply {
            numColumns = 4
            verticalSpacing = dp(context, 16)
            gravity = Gravity.CENTER
            clipToPadding = false
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            adapter = HomeAppAdapter(context, apps, onAppClick)
        }

        container.addView(gridView)
    }

    private fun dp(context: android.content.Context, value: Int): Int =
        (value * context.resources.displayMetrics.density).toInt()
}
