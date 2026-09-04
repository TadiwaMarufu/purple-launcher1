package com.thepurpleweb.purplelauncher.widgets

import android.content.Context
import com.thepurpleweb.purplelauncher.nativewidgets.NativeWidgetType

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

    fun getSavedNativeWidgetType(): NativeWidgetType? {
        val name = prefs.getString(KEY_NATIVE_WIDGET_TYPE, null) ?: return null
        return try {
            NativeWidgetType.valueOf(name)
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    fun saveNativeWidgetType(type: NativeWidgetType) {
        prefs.edit()
            .putString(KEY_NATIVE_WIDGET_TYPE, type.name)
            .apply()
    }

    fun clearNativeWidgetType() {
        prefs.edit()
            .remove(KEY_NATIVE_WIDGET_TYPE)
            .apply()
    }

    companion object {
        private const val KEY_WIDGET_ID = "saved_widget_id"
        private const val KEY_NATIVE_WIDGET_TYPE = "saved_native_widget_type"
        const val HOST_ID = 1024
    }
}
