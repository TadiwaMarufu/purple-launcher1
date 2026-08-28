package com.thepurpleweb.purplelauncher.settings

data class LauncherSettings(
    val showAppLabels: Boolean = true,
    val showDockLabels: Boolean = false,
    val vibrationEnabled: Boolean = true,
    val animationsEnabled: Boolean = true,
    val reducedMotion: Boolean = false,
    val smartDockEnabled: Boolean = true,
    val appDrawerSearchEnabled: Boolean = true
)
