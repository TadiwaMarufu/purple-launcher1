package com.thepurpleweb.purplelauncher.gestures

import android.content.Context

class GestureRepository(context: Context) {

    private val preferences =
        context.applicationContext.getSharedPreferences(
            "purple_gestures",
            Context.MODE_PRIVATE
        )

    fun getAction(
        gesture: GestureType
    ): GestureAction {

        val value = preferences.getString(
            keyFor(gesture),
            defaultFor(gesture).name
        ) ?: defaultFor(gesture).name

        return try {
            GestureAction.valueOf(value)
        } catch (_: IllegalArgumentException) {
            defaultFor(gesture)
        }
    }

    fun setAction(
        gesture: GestureType,
        action: GestureAction
    ) {
        preferences.edit()
            .putString(
                keyFor(gesture),
                action.name
            )
            .apply()
    }

    private fun keyFor(
        gesture: GestureType
    ): String =
        "gesture_${gesture.name.lowercase()}"

    private fun defaultFor(
        gesture: GestureType
    ): GestureAction =
        when (gesture) {
            GestureType.SWIPE_UP ->
                GestureAction.OPEN_APP_DRAWER

            GestureType.SWIPE_DOWN ->
                GestureAction.OPEN_NOTIFICATIONS

            GestureType.SWIPE_LEFT ->
                GestureAction.NONE

            GestureType.SWIPE_RIGHT ->
                GestureAction.NONE

            GestureType.DOUBLE_TAP ->
                GestureAction.OPEN_SEARCH

            GestureType.LONG_PRESS ->
                GestureAction.OPEN_SETTINGS
        }
}
