package com.thepurpleweb.purplelauncher.gestures

import android.view.MotionEvent
import kotlin.math.abs

class LauncherGestureDetector(
    private val repository: GestureRepository,
    private val onAction: (GestureAction) -> Unit
) {

    private var downX = 0f
    private var downY = 0f
    private var downTime = 0L

    private var lastTapTime = 0L

    fun onTouchEvent(
        event: MotionEvent
    ): Boolean {

        when (event.actionMasked) {

            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                downTime = System.currentTimeMillis()
                return true
            }

            MotionEvent.ACTION_UP -> {

                val upX = event.x
                val upY = event.y

                val deltaX = upX - downX
                val deltaY = upY - downY

                val distanceX = abs(deltaX)
                val distanceY = abs(deltaY)

                val elapsed =
                    System.currentTimeMillis() - downTime

                val now =
                    System.currentTimeMillis()

                if (
                    elapsed >= LONG_PRESS_MS &&
                    distanceX < TAP_SLOP &&
                    distanceY < TAP_SLOP
                ) {
                    perform(
                        GestureType.LONG_PRESS
                    )

                    return true
                }

                if (
                    distanceX < TAP_SLOP &&
                    distanceY < TAP_SLOP
                ) {

                    if (
                        now - lastTapTime <=
                        DOUBLE_TAP_TIMEOUT_MS
                    ) {
                        perform(
                            GestureType.DOUBLE_TAP
                        )

                        lastTapTime = 0L
                    } else {
                        lastTapTime = now
                    }

                    return true
                }

                if (
                    maxOf(
                        distanceX,
                        distanceY
                    ) >= SWIPE_THRESHOLD
                ) {

                    val gesture =
                        if (distanceX > distanceY) {

                            if (deltaX > 0) {
                                GestureType.SWIPE_RIGHT
                            } else {
                                GestureType.SWIPE_LEFT
                            }

                        } else {

                            if (deltaY > 0) {
                                GestureType.SWIPE_DOWN
                            } else {
                                GestureType.SWIPE_UP
                            }
                        }

                    perform(gesture)

                    return true
                }
            }
        }

        return false
    }

    private fun perform(
        gesture: GestureType
    ) {
        val action =
            repository.getAction(gesture)

        if (action != GestureAction.NONE) {
            onAction(action)
        }
    }

    companion object {
        private const val SWIPE_THRESHOLD = 120f
        private const val TAP_SLOP = 40f
        private const val LONG_PRESS_MS = 550L
        private const val DOUBLE_TAP_TIMEOUT_MS = 300L
    }
}
