package com.thepurpleweb.purplelauncher.nowbar

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat

class NowBarCallManager(
    private val context: Context,
    private val onChanged: (NowBarItem?) -> Unit
) {

    private val telephonyManager =
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    private var listening = false

    private val listener =
        object : android.telephony.PhoneStateListener() {

            override fun onCallStateChanged(
                state: Int,
                phoneNumber: String?
            ) {
                when (state) {
                    TelephonyManager.CALL_STATE_RINGING -> {
                        onChanged(
                            NowBarItem(
                                type = NowBarType.CALL,
                                title = "Incoming call",
                                subtitle = phoneNumber ?: "Incoming"
                            )
                        )
                    }

                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        onChanged(
                            NowBarItem(
                                type = NowBarType.CALL,
                                title = "Call in progress",
                                subtitle = phoneNumber ?: "Active call"
                            )
                        )
                    }

                    TelephonyManager.CALL_STATE_IDLE -> {
                        onChanged(null)
                    }
                }
            }
        }

    private fun hasPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED

    fun start() {

        if (listening) {
            return
        }

        if (!hasPermission()) {
            // Gracefully degrade: no permission, no call detection,
            // rest of the launcher is unaffected.
            return
        }

        try {
            @Suppress("DEPRECATION")
            telephonyManager.listen(
                listener,
                android.telephony.PhoneStateListener.LISTEN_CALL_STATE
            )
            listening = true
        } catch (_: SecurityException) {
            // Permission revoked between check and call, or OEM
            // restriction — degrade gracefully rather than crash.
        }
    }

    fun stop() {

        if (!listening) {
            return
        }

        try {
            @Suppress("DEPRECATION")
            telephonyManager.listen(
                listener,
                android.telephony.PhoneStateListener.LISTEN_NONE
            )
        } catch (_: Exception) {
        }

        listening = false
    }
}
