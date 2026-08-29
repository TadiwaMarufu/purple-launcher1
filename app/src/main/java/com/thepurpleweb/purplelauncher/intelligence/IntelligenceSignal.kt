package com.thepurpleweb.purplelauncher.intelligence

/**
 * Signals that Adaptive Intelligence is allowed to consider.
 *
 * These are deliberately explicit so the user can eventually
 * enable/disable individual sources.
 */
enum class IntelligenceSignal {
    TIME_PATTERN,
    APP_USAGE,
    BATTERY,
    MEDIA,
    CALENDAR,
    NOTIFICATIONS,
    LOCATION
}
