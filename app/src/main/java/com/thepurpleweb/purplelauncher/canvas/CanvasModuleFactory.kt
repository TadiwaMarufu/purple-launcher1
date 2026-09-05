package com.thepurpleweb.purplelauncher.canvas

import android.content.Context
import com.thepurpleweb.purplelauncher.nativewidgets.BatteryWidgetView
import com.thepurpleweb.purplelauncher.nativewidgets.ClockWidgetView
import com.thepurpleweb.purplelauncher.nativewidgets.NativeWidgetView

object CanvasModuleFactory {

    fun createContent(type: CanvasModuleType, context: Context): NativeWidgetView {
        return when (type) {
            CanvasModuleType.CLOCK -> ClockWidgetView(context)
            CanvasModuleType.BATTERY -> BatteryWidgetView(context)
            CanvasModuleType.MEDIA -> CanvasMediaModuleView(context)
            CanvasModuleType.QUOTE -> CanvasQuoteModuleView(context)
        }
    }
}
