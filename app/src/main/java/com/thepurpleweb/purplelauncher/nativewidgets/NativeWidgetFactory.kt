package com.thepurpleweb.purplelauncher.nativewidgets

import android.content.Context

object NativeWidgetFactory {
    fun create(type: NativeWidgetType, context: Context): NativeWidgetView = when (type) {
        NativeWidgetType.CLOCK -> ClockWidgetView(context)
        NativeWidgetType.BATTERY -> BatteryWidgetView(context)
        NativeWidgetType.NOTES -> NotesWidgetView(context)
    }
}
