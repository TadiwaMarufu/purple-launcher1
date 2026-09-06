package com.thepurpleweb.purplelauncher.canvas

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView

class FreeformModuleWrapper(
    context: Context,
    val moduleId: String
) : FrameLayout(context) {

    var isEditMode: Boolean = false
        set(value) {
            field = value
            resizeHandle.visibility = if (value) VISIBLE else GONE
            deleteHandle.visibility = if (value) VISIBLE else GONE
        }

    var onMoved: ((newXPx: Float, newYPx: Float) -> Unit)? = null
    var onResized: ((newWidthPx: Int, newHeightPx: Int) -> Unit)? = null
    var onDeleteRequested: (() -> Unit)? = null

    private var dragStartRawX = 0f
    private var dragStartRawY = 0f
    private var dragStartViewX = 0f
    private var dragStartViewY = 0f

    private val minWidthPx = (60 * resources.displayMetrics.density).toInt()
    private val minHeightPx = (40 * resources.displayMetrics.density).toInt()

    private val resizeHandle = View(context).apply {
        setBackgroundColor(Color.WHITE)
        visibility = GONE
    }

    private val deleteHandle = TextView(context).apply {
        text = "\u2715"
        textSize = 12f
        setTextColor(Color.WHITE)
        gravity = Gravity.CENTER
        setBackgroundColor(Color.rgb(180, 40, 40))
        visibility = GONE
    }

    init {
        val handleSizePx = (18 * resources.displayMetrics.density).toInt()

        addView(
            resizeHandle,
            LayoutParams(handleSizePx, handleSizePx, Gravity.BOTTOM or Gravity.END)
        )

        addView(
            deleteHandle,
            LayoutParams(handleSizePx, handleSizePx, Gravity.TOP or Gravity.END)
        )

        deleteHandle.setOnClickListener {
            if (isEditMode) {
                onDeleteRequested?.invoke()
            }
        }

        resizeHandle.setOnTouchListener { _, event ->
            if (!isEditMode) return@setOnTouchListener false

            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    dragStartRawX = event.rawX
                    dragStartRawY = event.rawY
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - dragStartRawX
                    val dy = event.rawY - dragStartRawY

                    val newWidth = (width + dx).toInt().coerceAtLeast(minWidthPx)
                    val newHeight = (height + dy).toInt().coerceAtLeast(minHeightPx)

                    layoutParams = layoutParams.apply {
                        width = newWidth
                        height = newHeight
                    }
                    requestLayout()

                    dragStartRawX = event.rawX
                    dragStartRawY = event.rawY
                    true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    onResized?.invoke(width, height)
                    true
                }

                else -> false
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return isEditMode
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEditMode) return false

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                dragStartRawX = event.rawX
                dragStartRawY = event.rawY
                dragStartViewX = x
                dragStartViewY = y
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX - dragStartRawX
                val dy = event.rawY - dragStartRawY
                x = dragStartViewX + dx
                y = dragStartViewY + dy
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                onMoved?.invoke(x, y)
                return true
            }
        }
        return false
    }
}
