package com.thepurpleweb.purplelauncher.home

import android.graphics.Color
import android.text.format.DateFormat
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import com.thepurpleweb.purplelauncher.apps.AppInfo
import com.thepurpleweb.purplelauncher.search.SearchResultAdapter
import java.util.Calendar

/**
 * Focus: productivity dashboard. Time up top, apps as a fast
 * scannable list rather than a grid — information-oriented,
 * efficient navigation, per spec section 5/1.
 */
class FocusHomeLayout : HomeLayout {

    override fun build(
        container: ViewGroup,
        apps: List<AppInfo>,
        onAppClick: (AppInfo) -> Unit
    ) {
        container.removeAllViews()

        val context = container.context

        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val clock = TextView(context).apply {
            text = DateFormat.format("h:mm a", Calendar.getInstance()).toString()
            textSize = 40f
            setTextColor(Color.WHITE)
            setPadding(dp(context, 20), 0, 0, dp(context, 4))
        }

        val label = TextView(context).apply {
            text = "Your apps"
            textSize = 13f
            setTextColor(Color.rgb(150, 150, 150))
            setPadding(dp(context, 20), 0, 0, dp(context, 12))
        }

        val listView = ListView(context).apply {
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
    }

    private fun dp(context: android.content.Context, value: Int): Int =
        (value * context.resources.displayMetrics.density).toInt()
}
