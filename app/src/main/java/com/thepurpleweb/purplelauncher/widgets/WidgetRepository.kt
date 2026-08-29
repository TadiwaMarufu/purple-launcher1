package com.thepurpleweb.purplelauncher.widgets

import android.content.Context

class WidgetRepository(context: Context) {

    private val prefs =
        context.applicationContext.getSharedPreferences(
            "purple_widgets",
            Context.MODE_PRIVATE
        )

    fun getSavedWidgetId(): Int =
        prefs.getInt(KEY_WIDGET_ID, -1)

    fun saveWidgetId(appWidgetId: Int) {
        prefs.edit()
            .putInt(KEY_WIDGET_ID, appWidgetId)
            .apply()
    }

    fun clearWidgetId() {
        prefs.edit()
            .remove(KEY_WIDGET_ID)
            .apply()
    }

    companion object {
        private const val KEY_WIDGET_ID = "saved_widget_id"
        const val HOST_ID = 1024
    }
}
