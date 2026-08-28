package com.thepurpleweb.purplelauncher.profile

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileEngine(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("purple_launcher_prefs", Context.MODE_PRIVATE)

    private val _current = MutableStateFlow(loadSavedProfile())
    val current: StateFlow<Profile> = _current

    private fun loadSavedProfile(): Profile {
        val savedId = prefs.getString(KEY_PROFILE, Profile.Calm.id) ?: Profile.Calm.id
        return Profile.fromId(savedId)
    }

    fun setProfile(profile: Profile) {
        _current.value = profile
        prefs.edit().putString(KEY_PROFILE, profile.id).apply()
    }

    fun cycleNext() {
        val idx = Profile.all.indexOf(_current.value)
        val next = Profile.all[(idx + 1) % Profile.all.size]
        setProfile(next)
    }

    companion object {
        private const val KEY_PROFILE = "current_profile"
    }
}
