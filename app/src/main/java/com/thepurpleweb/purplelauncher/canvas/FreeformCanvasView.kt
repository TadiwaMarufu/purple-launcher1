package com.thepurpleweb.purplelauncher.canvas

import android.content.Context
import android.widget.FrameLayout

class FreeformCanvasView(context: Context) : FrameLayout(context) {

    private val wrappers = mutableMapOf<String, FreeformModuleWrapper>()

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

    fun clearModules() {
        wrappers.values.forEach { removeView(it) }
        wrappers.clear()
    }
}
