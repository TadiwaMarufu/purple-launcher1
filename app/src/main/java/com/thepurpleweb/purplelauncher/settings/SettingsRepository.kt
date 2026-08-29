package com.thepurpleweb.purplelauncher.settings

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(
    context: Context
) {

    private val preferences =
        context.getSharedPreferences(
            "launcher_settings",
            Context.MODE_PRIVATE
        )

    private val _settings =
        MutableStateFlow(loadSettings())

    val settings: StateFlow<LauncherSettings> =
        _settings.asStateFlow()

    private fun loadSettings(): LauncherSettings {
        return LauncherSettings(
            showAppLabels =
                preferences.getBoolean(
                    KEY_SHOW_APP_LABELS,
                    true
                ),

            showDockLabels =
                preferences.getBoolean(
                    KEY_SHOW_DOCK_LABELS,
                    false
                ),

            vibrationEnabled =
                preferences.getBoolean(
                    KEY_VIBRATION,
                    true
                ),

            animationsEnabled =
                preferences.getBoolean(
                    KEY_ANIMATIONS,
                    true
                ),

            reducedMotion =
                preferences.getBoolean(
                    KEY_REDUCED_MOTION,
                    false
                ),

            smartDockEnabled =
                preferences.getBoolean(
                    KEY_SMART_DOCK,
                    true
                ),

            appDrawerSearchEnabled =
                preferences.getBoolean(
                    KEY_DRAWER_SEARCH,
                    true
                ),

            performanceMode =
                preferences.getBoolean(
                    KEY_PERFORMANCE_MODE,
                    true
                )
        )
    }

    fun setShowAppLabels(enabled: Boolean) {
        update(
            _settings.value.copy(
                showAppLabels = enabled
            )
        )
    }

    fun setShowDockLabels(enabled: Boolean) {
        update(
            _settings.value.copy(
                showDockLabels = enabled
            )
        )
    }

    fun setVibrationEnabled(enabled: Boolean) {
        update(
            _settings.value.copy(
                vibrationEnabled = enabled
            )
        )
    }

    fun setAnimationsEnabled(enabled: Boolean) {
        update(
            _settings.value.copy(
                animationsEnabled = enabled
            )
        )
    }

    fun setReducedMotion(enabled: Boolean) {
        update(
            _settings.value.copy(
                reducedMotion = enabled
            )
        )
    }

    fun setSmartDockEnabled(enabled: Boolean) {
        update(
            _settings.value.copy(
                smartDockEnabled = enabled
            )
        )
    }

    fun setAppDrawerSearchEnabled(enabled: Boolean) {
        update(
            _settings.value.copy(
                appDrawerSearchEnabled = enabled
            )
        )
    }

    fun setPerformanceMode(enabled: Boolean) {
        update(
            _settings.value.copy(
                performanceMode = enabled
            )
        )
    }

    private fun update(
        settings: LauncherSettings
    ) {

        preferences.edit()
            .putBoolean(
                KEY_SHOW_APP_LABELS,
                settings.showAppLabels
            )
            .putBoolean(
                KEY_SHOW_DOCK_LABELS,
                settings.showDockLabels
            )
            .putBoolean(
                KEY_VIBRATION,
                settings.vibrationEnabled
            )
            .putBoolean(
                KEY_ANIMATIONS,
                settings.animationsEnabled
            )
            .putBoolean(
                KEY_REDUCED_MOTION,
                settings.reducedMotion
            )
            .putBoolean(
                KEY_SMART_DOCK,
                settings.smartDockEnabled
            )
            .putBoolean(
                KEY_DRAWER_SEARCH,
                settings.appDrawerSearchEnabled
            )
            .putBoolean(
                KEY_PERFORMANCE_MODE,
                settings.performanceMode
            )
            .apply()

        _settings.value = settings
    }

    companion object {

        private const val KEY_SHOW_APP_LABELS =
            "show_app_labels"

        private const val KEY_SHOW_DOCK_LABELS =
            "show_dock_labels"

        private const val KEY_VIBRATION =
            "vibration_enabled"

        private const val KEY_ANIMATIONS =
            "animations_enabled"

        private const val KEY_REDUCED_MOTION =
            "reduced_motion"

        private const val KEY_SMART_DOCK =
            "smart_dock_enabled"

        private const val KEY_DRAWER_SEARCH =
            "drawer_search_enabled"

        private const val KEY_PERFORMANCE_MODE =
            "performance_mode"
    }
}
