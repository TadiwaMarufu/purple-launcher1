package com.thepurpleweb.purplelauncher.widgets.native

import android.view.View
import android.view.ViewGroup
import com.thepurpleweb.purplelauncher.performance.VisualQuality
import com.thepurpleweb.purplelauncher.profile.ProfileDesign

interface PurpleWidget {

    val id: String

    fun createView(
        parent: ViewGroup,
        design: ProfileDesign,
        quality: VisualQuality
    ): View

    fun onStart()

    fun onStop()
}
