package com.thepurpleweb.purplelauncher.home

import android.graphics.Color
import android.text.format.DateFormat
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import com.thepurpleweb.purplelauncher.apps.AppInfo
import com.thepurpleweb.purplelauncher.performance.VisualQuality
import com.thepurpleweb.purplelauncher.search.SearchResultAdapter
import java.util.Calendar

class FocusHomeLayout : HomeLayout {

    override fun build(
        container: ViewGroup,
        apps: List<AppInfo>,
        quality: VisualQuality,
        reducedMotion: Boolean,
        onAppClick: (AppInfo) -> Unit
    ) {
        container.removeAllViews()

        val context = container.context
        val visuals = ProfileVisualsProvider.forMotion(MotionStyle.FOCUS)

        container.setBackgroundColor(ProfileVisualsProvider.withAlpha(visuals.background, ProfileVisualsProvider.scrimAlphaFor(MotionStyle.FOCUS)))

        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                ProfileVisualsProvider.dp(this, visuals.horizontalPaddingDp),
                8,
                ProfileVisualsProvider.dp(this, visuals.horizontalPaddingDp),
                0
            )
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val clock = TextView(context).apply {
            text = DateFormat.format("h:mm a", Calendar.getInstance()).toString()
            textSize = visuals.titleSizeSp
            setTextColor(visuals.primaryText)
            setPadding(0, 0, 0, ProfileVisualsProvider.dp(this, 4))
        }

        val label = TextView(context).apply {
            text = "Your apps"
            textSize = visuals.bodySizeSp
            setTextColor(visuals.secondaryText)
            setPadding(0, 0, 0, ProfileVisualsProvider.dp(this, 12))
        }

        val listView = ListView(context).apply {
            isVerticalScrollBarEnabled = false
            cacheColorHint = Color.TRANSPARENT
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            divider = null
            dividerHeight = 0
            adapter = SearchResultAdapter(context, apps, onAppClick)
        }

        root.addView(clock)
        root.addView(label)
        root.addView(listView)

        container.addView(root)

        ProfileVisualsProvider.animate(root, MotionStyle.FOCUS, quality, reducedMotion)
        ProfileVisualsProvider.animateChildren(listView, MotionStyle.FOCUS, quality, reducedMotion)
    }
}
