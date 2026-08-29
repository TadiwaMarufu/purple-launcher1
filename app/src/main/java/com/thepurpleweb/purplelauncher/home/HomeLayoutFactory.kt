package com.thepurpleweb.purplelauncher.home

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.thepurpleweb.purplelauncher.apps.AppInfo
import com.thepurpleweb.purplelauncher.profile.Profile

interface HomeLayout {
    fun build(
        container: LinearLayout,
        apps: List<AppInfo>,
        onAppClick: (AppInfo) -> Unit
    )
}

object HomeLayoutFactory {

    fun forProfile(profile: Profile): HomeLayout {
        return when (profile) {
            Profile.Fluid -> FluidHomeLayout()
            Profile.Premium -> PremiumHomeLayout()
            Profile.Calm -> CalmHomeLayout()
            Profile.Focus -> FocusHomeLayout()
            Profile.Expressive -> ExpressiveHomeLayout()
        }
    }
}

private abstract class BaseHomeLayout : HomeLayout {

    protected fun clear(container: LinearLayout) {
        container.removeAllViews()
        container.orientation = LinearLayout.VERTICAL
        container.gravity = Gravity.CENTER_HORIZONTAL
        container.setPadding(16, 16, 16, 16)
    }

    protected fun addTitle(
        container: LinearLayout,
        title: String,
        subtitle: String
    ) {
        val titleView = TextView(container.context).apply {
            text = title
            textSize = 28f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val subtitleView = TextView(container.context).apply {
            text = subtitle
            textSize = 13f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 4, 0, 20)
        }

        container.addView(
            titleView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        container.addView(
            subtitleView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
    }

    protected fun addApps(
        container: LinearLayout,
        apps: List<AppInfo>,
        onAppClick: (AppInfo) -> Unit,
        columns: Int
    ) {
        val grid = android.widget.GridLayout(container.context).apply {
            columnCount = columns
            rowCount = ((apps.size + columns - 1) / columns)
                .coerceAtLeast(1)
            useDefaultMargins = true
        }

        apps.forEach { app ->
            val item = TextView(container.context).apply {
                text = app.label
                textSize = 13f
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
                setPadding(10, 18, 10, 18)
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    onAppClick(app)
                }
            }

            val params = android.widget.GridLayout.LayoutParams().apply {
                width = 0
                height = android.widget.GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec =
                    android.widget.GridLayout.spec(
                        android.widget.GridLayout.UNDEFINED,
                        1f
                    )
            }

            grid.addView(item, params)
        }

        container.addView(
            grid,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
    }
}

private class FluidHomeLayout : BaseHomeLayout() {

    override fun build(
        container: LinearLayout,
        apps: List<AppInfo>,
        onAppClick: (AppInfo) -> Unit
    ) {
        clear(container)
        addTitle(
            container,
            "Fluid",
            "Smooth, adaptive and flowing."
        )
        addApps(container, apps.take(8), onAppClick, 4)
    }
}

private class PremiumHomeLayout : BaseHomeLayout() {

    override fun build(
        container: LinearLayout,
        apps: List<AppInfo>,
        onAppClick: (AppInfo) -> Unit
    ) {
        clear(container)
        addTitle(
            container,
            "Premium",
            "Polished, refined and expressive."
        )
        addApps(container, apps.take(8), onAppClick, 4)
    }
}

private class CalmHomeLayout : BaseHomeLayout() {

    override fun build(
        container: LinearLayout,
        apps: List<AppInfo>,
        onAppClick: (AppInfo) -> Unit
    ) {
        clear(container)
        addTitle(
            container,
            "Calm",
            "Quiet, minimal and distraction-free."
        )
        addApps(container, apps.take(4), onAppClick, 2)
    }
}

private class FocusHomeLayout : BaseHomeLayout() {

    override fun build(
        container: LinearLayout,
        apps: List<AppInfo>,
        onAppClick: (AppInfo) -> Unit
    ) {
        clear(container)
        addTitle(
            container,
            "Focus",
            "Structured for productivity and concentration."
        )
        addApps(container, apps.take(6), onAppClick, 3)
    }
}

private class ExpressiveHomeLayout : BaseHomeLayout() {

    override fun build(
        container: LinearLayout,
        apps: List<AppInfo>,
        onAppClick: (AppInfo) -> Unit
    ) {
        clear(container)
        addTitle(
            container,
            "Expressive",
            "Bold, energetic and highly visual."
        )
        addApps(container, apps.take(12), onAppClick, 4)
    }
}
