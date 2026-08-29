package com.thepurpleweb.purplelauncher.intelligence

/**
 * Lightweight snapshot of the current launcher context.
 *
 * This object intentionally contains no Android Views and no UI logic.
 */
data class LauncherContext(
    val hourOfDay: Int,
    val batteryPercent: Int,
    val isCharging: Boolean,
    val isMediaPlaying: Boolean = false
) {

    val isMorning: Boolean
        get() = hourOfDay in 5..10

    val isDaytime: Boolean
        get() = hourOfDay in 11..17

    val isEvening: Boolean
        get() = hourOfDay in 18..21

    val isNight: Boolean
        get() = hourOfDay >= 22 || hourOfDay < 5

    val batteryCritical: Boolean
        get() = batteryPercent in 0..15
}
