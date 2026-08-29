package com.thepurpleweb.purplelauncher.profile

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Single source of truth for the currently active Purple Launcher profile.
 *
 * ProfileEngine is process-wide so MainActivity and ProfilesActivity observe
 * the same StateFlow. SharedPreferences remains the persistent source of
 * truth across process death/restarts.
 */
class ProfileEngine private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    private val _current = MutableStateFlow(loadSavedProfile())
    val current: StateFlow<Profile> = _current

    private val preferenceListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_PROFILE) {
                val savedId = prefs.getString(KEY_PROFILE, Profile.Calm.id)
                    ?: Profile.Calm.id

                val profile = Profile.fromId(savedId)

                if (_current.value.id != profile.id) {
                    _current.value = profile
                }
            }
        }

    init {
        prefs.registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    private fun loadSavedProfile(): Profile {
        val savedId = prefs.getString(
            KEY_PROFILE,
            Profile.Calm.id
        ) ?: Profile.Calm.id

        return Profile.fromId(savedId)
    }

    /**
     * Changes the active profile immediately and persists it.
     *
     * Updating the StateFlow first makes the currently running launcher
     * react immediately. SharedPreferences then keeps the choice after
     * activity recreation, process death, or device reboot.
     */
    fun setProfile(profile: Profile) {
        if (_current.value.id != profile.id) {
            _current.value = profile
        }

        prefs.edit()
            .putString(KEY_PROFILE, profile.id)
            .apply()
    }

    fun cycleNext() {
        val idx = Profile.all.indexOfFirst {
            it.id == _current.value.id
        }

        val nextIndex = if (idx >= 0) {
            (idx + 1) % Profile.all.size
        } else {
            0
        }

        setProfile(Profile.all[nextIndex])
    }

    companion object {

        private const val PREFS_NAME = "purple_launcher_prefs"
        private const val KEY_PROFILE = "current_profile"

        @Volatile
        private var INSTANCE: ProfileEngine? = null

        fun getInstance(context: Context): ProfileEngine {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ProfileEngine(
                    context.applicationContext
                ).also {
                    INSTANCE = it
                }
            }
        }
    }
}
