package com.thepurpleweb.purplelauncher.nowbar

import android.os.Handler
import android.os.Looper
import java.util.Locale

class NowBarTimerManager(
    private val onChanged: (NowBarItem?) -> Unit
) {

    private val handler =
        Handler(Looper.getMainLooper())

    private var endAt: Long = 0L

    private var running = false

    private val tick =
        object : Runnable {
            override fun run() {

                if (!running) {
                    return
                }

                val remaining =
                    endAt - System.currentTimeMillis()

                if (remaining <= 0L) {

                    running = false
                    endAt = 0L

                    onChanged(null)

                    return
                }

                onChanged(
                    NowBarItem(
                        type = NowBarType.TIMER,
                        title = "Timer",
                        subtitle = formatRemaining(remaining),
                        progress = null,
                        isPersistent = true
                    )
                )

                handler.postDelayed(
                    this,
                    1000L
                )
            }
        }

    fun start(
        durationMillis: Long
    ) {

        stop()

        if (durationMillis <= 0L) {
            onChanged(null)
            return
        }

        endAt =
            System.currentTimeMillis() +
                durationMillis

        running = true

        handler.post(tick)
    }

    fun stop() {

        running = false
        endAt = 0L

        handler.removeCallbacks(tick)

        onChanged(null)
    }

    fun isRunning(): Boolean =
        running

    fun remainingMillis(): Long {

        if (!running) {
            return 0L
        }

        return (
            endAt -
                System.currentTimeMillis()
            ).coerceAtLeast(0L)
    }

    private fun formatRemaining(
        millis: Long
    ): String {

        val totalSeconds =
            (millis / 1000L)
                .coerceAtLeast(0L)

        val hours =
            totalSeconds / 3600L

        val minutes =
            (totalSeconds % 3600L) / 60L

        val seconds =
            totalSeconds % 60L

        return if (hours > 0L) {

            String.format(
                Locale.US,
                "%d:%02d:%02d",
                hours,
                minutes,
                seconds
            )

        } else {

            String.format(
                Locale.US,
                "%02d:%02d",
                minutes,
                seconds
            )
        }
    }
}
