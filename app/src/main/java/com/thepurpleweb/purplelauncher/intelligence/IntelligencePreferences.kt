package com.thepurpleweb.purplelauncher.intelligence

import android.content.Context

/**
 * Stores user-controlled intelligence permissions.
 *
 * Adaptive Intelligence is local-first. Nothing here uploads
 * usage information anywhere.
 */
class IntelligencePreferences(context: Context) {

    companion object {
        private const val PREFS = "purple_intelligence"

        private const val KEY_TIME = "signal_time"
        private const val KEY_USAGE = "signal_usage"
        private const val KEY_BATTERY = "signal_battery"
        private const val KEY_MEDIA = "signal_media"
        private const val KEY_CALENDAR = "signal_calendar"
        private const val KEY_NOTIFICATIONS = "signal_notifications"
        private const val KEY_LOCATION = "signal_location"
    }

    private val preferences =
        context.applicationContext.getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        )

    fun isEnabled(signal: IntelligenceSignal): Boolean {
        return preferences.getBoolean(
            keyFor(signal),
            defaultFor(signal)
        )
    }

    fun setEnabled(
        signal: IntelligenceSignal,
        enabled: Boolean
    ) {
        preferences.edit()
            .putBoolean(
                keyFor(signal),
                enabled
            )
            .apply()
    }

    private fun keyFor(signal: IntelligenceSignal): String {
        return when (signal) {
            IntelligenceSignal.TIME_PATTERN -> KEY_TIME
            IntelligenceSignal.APP_USAGE -> KEY_USAGE
            IntelligenceSignal.BATTERY -> KEY_BATTERY
            IntelligenceSignal.MEDIA -> KEY_MEDIA
            IntelligenceSignal.CALENDAR -> KEY_CALENDAR
            IntelligenceSignal.NOTIFICATIONS -> KEY_NOTIFICATIONS
            IntelligenceSignal.LOCATION -> KEY_LOCATION
        }
    }

    private fun defaultFor(signal: IntelligenceSignal): Boolean {
        return when (signal) {
            IntelligenceSignal.LOCATION -> false
            else -> true
        }
    }
}
