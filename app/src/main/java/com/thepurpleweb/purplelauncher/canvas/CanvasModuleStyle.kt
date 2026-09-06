package com.thepurpleweb.purplelauncher.canvas

import android.graphics.Color

/**
 * Shared visual language for canvas/native-widget modules. Deliberately
 * profile-agnostic for now — a genuine per-profile canvas palette
 * (matching Home's ProfileVisualsProvider) is real future work, not
 * done in this pass.
 */
object CanvasModuleStyle {
    val cardBackground = Color.rgb(20, 20, 24)
    val primaryText = Color.WHITE
    val secondaryText = Color.rgb(150, 150, 158)
    val accent = Color.rgb(190, 140, 255)
    val trackColor = Color.rgb(48, 48, 54)
    const val cornerRadiusDp = 20f
}
