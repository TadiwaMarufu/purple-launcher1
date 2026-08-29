package com.thepurpleweb.purplelauncher.widgets.native

object PurpleWidgetRegistry {

    private val widgets: Map<PurpleWidgetType, () -> PurpleWidget> = mapOf(
        PurpleWidgetType.CLOCK to { PurpleClockWidget() }
    )

    fun create(type: PurpleWidgetType): PurpleWidget? =
        widgets[type]?.invoke()
}
