package com.thepurpleweb.purplelauncher.canvas

/**
 * Position/size are stored in dp, not px, so saved layouts remain
 * consistent across devices with different screen densities.
 */
data class CanvasModuleState(
    val id: String,
    val type: CanvasModuleType,
    var xDp: Float,
    var yDp: Float,
    var widthDp: Float,
    var heightDp: Float
)
