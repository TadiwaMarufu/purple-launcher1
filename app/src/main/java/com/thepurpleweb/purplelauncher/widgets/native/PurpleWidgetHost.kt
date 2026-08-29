package com.thepurpleweb.purplelauncher.widgets.native

import android.view.ViewGroup
import com.thepurpleweb.purplelauncher.performance.VisualQuality
import com.thepurpleweb.purplelauncher.profile.ProfileDesign

class PurpleWidgetHost(
    private val container: ViewGroup
) {

    private var currentWidget: PurpleWidget? = null

    fun show(
        type: PurpleWidgetType,
        design: ProfileDesign,
        quality: VisualQuality
    ) {
        currentWidget?.onStop()

        container.removeAllViews()

        val widget = PurpleWidgetRegistry.create(type) ?: return

        val view = widget.createView(
            container,
            design,
            quality
        )

        container.addView(view)

        currentWidget = widget
        widget.onStart()
    }

    fun clear() {
        currentWidget?.onStop()
        currentWidget = null
        container.removeAllViews()
    }

    fun onStart() {
        currentWidget?.onStart()
    }

    fun onStop() {
        currentWidget?.onStop()
    }
}
