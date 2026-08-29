package com.thepurpleweb.purplelauncher.nowbar

import android.content.Context

class NowBarRepository(context: Context) {

    companion object {
        private const val PREFS = "purple_now_bar"
        private const val KEY_ENABLED = "enabled"
    }

    private val preferences =
        context.applicationContext.getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        )

    fun isEnabled(): Boolean =
        preferences.getBoolean(
            KEY_ENABLED,
            true
        )

    fun setEnabled(enabled: Boolean) {
        preferences.edit()
            .putBoolean(
                KEY_ENABLED,
                enabled
            )
            .apply()
    }
}
