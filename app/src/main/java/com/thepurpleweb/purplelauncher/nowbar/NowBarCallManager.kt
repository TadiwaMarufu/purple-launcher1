package com.thepurpleweb.purplelauncher.nowbar

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager

class NowBarCallManager(
    private val context: Context,
    private val onCallStateChanged: (NowBarItem?) -> Unit
) {
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    private var callStartTime = 0L
    private val handler = Handler(Looper.getMainLooper())
    private var isTracking = false

    private val tickerRunnable = object : Runnable {
        override fun run() {
            if (!isTracking) return
            val elapsedSeconds = (System.currentTimeMillis() - callStartTime) / 1000
            val minutes = elapsedSeconds / 60
            val seconds = elapsedSeconds % 60
            val timeString = String.format("%02d:%02d", minutes, seconds)

            onCallStateChanged(
                NowBarItem(
                    type = NowBarType.CALL,
                    title = "Active Call",
                    subtitle = timeString,
                    isPersistent = true
                )
            )
            handler.postDelayed(this, 1000)
        }
    }

    private val listener = object : PhoneStateListener() {
        @Deprecated("Deprecated in Java")
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    isTracking = false
                    handler.removeCallbacks(tickerRunnable)
                    val caller = if (!phoneNumber.isNullOrEmpty()) phoneNumber else "Incoming Call"
                    onCallStateChanged(
                        NowBarItem(
                            type = NowBarType.CALL,
                            title = caller,
                            subtitle = "Ringing...",
                            isPersistent = true
                        )
                    )
                }
                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    callStartTime = System.currentTimeMillis()
                    isTracking = true
                    handler.post(tickerRunnable)
                }
                TelephonyManager.CALL_STATE_IDLE -> {
                    isTracking = false
                    handler.removeCallbacks(tickerRunnable)
                    onCallStateChanged(null)
                }
            }
        }
    }

    fun start() {
        try {
            telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE)
        } catch (_: Exception) {}
    }

    fun stop() {
        isTracking = false
        handler.removeCallbacks(tickerRunnable)
        try {
            telephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE)
        } catch (_: Exception) {}
    }
}
