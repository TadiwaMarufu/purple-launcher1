package com.thepurpleweb.purplelauncher.canvas

import android.content.Context
import android.widget.FrameLayout
import com.thepurpleweb.purplelauncher.nativewidgets.NativeWidgetView

class FreeformCanvasView(context: Context) : FrameLayout(context) {

    private val wrappers = mutableMapOf<String, FreeformModuleWrapper>()
    private val contents = mutableListOf<NativeWidgetView>()

    var isEditMode: Boolean = false
        set(value) {
            field = value
            wrappers.values.forEach { it.isEditMode = value }
        }

    fun addModule(
        state: CanvasModuleState,
        onStateChanged: (CanvasModuleState) -> Unit
    ) {
        val density = resources.displayMetrics.density
        val content = CanvasModuleFactory.createContent(state.type, context)
        contents += content

        val wrapper = FreeformModuleWrapper(context, state.id).apply {
            addView(
                content,
                0,
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            )

            layoutParams = LayoutParams(
                (state.widthDp * density).toInt(),
                (state.heightDp * density).toInt()
            )

            x = state.xDp * density
            y = state.yDp * density

            isEditMode = this@FreeformCanvasView.isEditMode

            onMoved = { newXPx, newYPx ->
                state.xDp = newXPx / density
                state.yDp = newYPx / density
                onStateChanged(state)
            }

            onResized = { newWidthPx, newHeightPx ->
                state.widthDp = newWidthPx / density
                state.heightDp = newHeightPx / density
                onStateChanged(state)
            }
        }

        wrappers[state.id] = wrapper
        addView(wrapper)
    }

    // Battery/Media modules own real resources (a receiver, a media
    // session listener) — these must only run while the canvas is
    // actually visible, same discipline as everywhere else in this
    // project. Called from MainActivity's onStart/onStop, plus whenever
    // the canvas itself is (re)built.
    fun startAll() {
        contents.forEach { it.start() }
    }

    fun stopAll() {
        contents.forEach { it.stop() }
    }

    fun clearModules() {
        stopAll()
        wrappers.values.forEach { removeView(it) }
        wrappers.clear()
        contents.clear()
    }
}
