package com.thepurpleweb.purplelauncher.profile

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Central profile state manager.
 *
 * Multiple launcher components may create ProfileEngine instances.
 * They all share the same process-wide StateFlow, so a profile change
 * made by ProfilesActivity is immediately visible to MainActivity,
 * adapters, notifications, search, and the Quick Settings tile.
 *
 * SharedPreferences remains the persistent source of truth across
 * activity recreation and process restarts.
 */
class ProfileEngine private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    /**
     * Every ProfileEngine instance exposes the SAME StateFlow.
     */
    val current: StateFlow<Profile>
        get() = sharedCurrent

    private val preferenceListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_PROFILE) {
                val savedId = prefs.getString(
                    KEY_PROFILE,
                    Profile.Calm.id
                ) ?: Profile.Calm.id

                val profile = Profile.fromId(savedId)

                if (sharedCurrent.value.id != profile.id) {
                    sharedCurrent.value = profile
                }
            }
        }

    init {
        /*
         * Synchronize the shared in-memory state with persistent state
         * whenever an engine is created.
         *
         * This is intentionally done without replacing the StateFlow,
         * so existing collectors remain subscribed.
         */
        val savedId = prefs.getString(
            KEY_PROFILE,
            Profile.Calm.id
        ) ?: Profile.Calm.id

        val savedProfile = Profile.fromId(savedId)

        if (sharedCurrent.value.id != savedProfile.id) {
            sharedCurrent.value = savedProfile
        }

        prefs.registerOnSharedPreferenceChangeListener(
            preferenceListener
        )
    }

    /**
     * Changes the active profile immediately for every component in the
     * current application process and persists the choice.
     */
    fun setProfile(profile: Profile) {
        /*
         * Update the shared StateFlow FIRST.
         *
         * MainActivity is normally collecting this value, so the home
         * screen can react immediately without waiting for an activity
         * restart.
         */
        if (sharedCurrent.value.id != profile.id) {
            sharedCurrent.value = profile
        }

        /*
         * Persist the selection so it survives:
         * - activity recreation
         * - launcher restart
         * - process death
         * - device reboot
         */
        prefs.edit()
            .putString(KEY_PROFILE, profile.id)
            .apply()
    }

    /**
     * Switch to the next profile in the defined profile order.
     */
    fun cycleNext() {
        val currentIndex = Profile.all.indexOfFirst {
            it.id == sharedCurrent.value.id
        }

        val nextIndex =
            if (currentIndex >= 0) {
                (currentIndex + 1) % Profile.all.size
            } else {
                0
            }

        setProfile(Profile.all[nextIndex])
    }

    companion object {

        private const val PREFS_NAME =
            "purple_launcher_prefs"

        private const val KEY_PROFILE =
            "current_profile"

        /*
         * One StateFlow for the entire application process.
         */
        private val sharedCurrent =
            MutableStateFlow<Profile>(Profile.Calm)

        /*
         * One ProfileEngine instance for the entire application process.
         *
         * Every launcher component must use getInstance() so they all
         * operate on the same persistent profile state.
         */
        @Volatile
        private var instance: ProfileEngine? = null

        fun getInstance(context: Context): ProfileEngine {
            return instance ?: synchronized(this) {
                instance ?: ProfileEngine(
                    context.applicationContext
                ).also {
                    instance = it
                }
            }
        }
    }
}
