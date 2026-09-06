package com.thepurpleweb.purplelauncher.canvas

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import com.thepurpleweb.purplelauncher.nativewidgets.NativeWidgetView

class FreeformCanvasView(context: Context) : FrameLayout(context) {

    private val wrappers = mutableMapOf<String, FreeformModuleWrapper>()
    private val contents = mutableListOf<NativeWidgetView>()

    var onAddRequested: (() -> Unit)? = null
    var onModuleDeleted: ((String) -> Unit)? = null

    private val addButton = TextView(context).apply {
        text = "+  Add module"
        textSize = 14f
        setTextColor(Color.WHITE)
        gravity = Gravity.CENTER
        setBackgroundColor(Color.rgb(60, 40, 80))
        setPadding(
            (24 * resources.displayMetrics.density).toInt(),
            (14 * resources.displayMetrics.density).toInt(),
            (24 * resources.displayMetrics.density).toInt(),
            (14 * resources.displayMetrics.density).toInt()
        )
        visibility = GONE
    }

    var isEditMode: Boolean = false
        set(value) {
            field = value
            wrappers.values.forEach { it.isEditMode = value }
            addButton.visibility = if (value) VISIBLE else GONE
        }

    init {
        addButton.setOnClickListener { onAddRequested?.invoke() }

        addView(
            addButton,
            LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            ).apply {
                bottomMargin = (24 * resources.displayMetrics.density).toInt()
            }
        )
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

            onDeleteRequested = {
                onModuleDeleted?.invoke(state.id)
            }
        }

        wrappers[state.id] = wrapper
        addView(wrapper)
        addButton.bringToFront()
    }

    fun removeModule(id: String) {
        val wrapper = wrappers.remove(id) ?: return

        for (i in 0 until wrapper.childCount) {
            val child = wrapper.getChildAt(i)
            if (child is NativeWidgetView) {
                child.stop()
                contents.remove(child)
            }
        }

        removeView(wrapper)
    }

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
