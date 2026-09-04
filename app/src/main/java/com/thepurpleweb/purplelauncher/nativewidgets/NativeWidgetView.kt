package com.thepurpleweb.purplelauncher.nativewidgets

import android.content.Context
import android.widget.FrameLayout

/**
 * Base for launcher-native "Layer 2" widgets (spec section 8) — distinct
 * from the real Android AppWidgetHost integration (Layer 1, Phase 8).
 * start()/stop() are called from MainActivity's lifecycle (onStart/onStop)
 * so any receivers/timers a widget owns don't run while the launcher
 * isn't visible — same discipline as appWidgetHost.startListening()/
 * stopListening() and the Now Bar managers.
 */
abstract class NativeWidgetView(context: Context) : FrameLayout(context) {
    abstract fun start()
    abstract fun stop()
}
