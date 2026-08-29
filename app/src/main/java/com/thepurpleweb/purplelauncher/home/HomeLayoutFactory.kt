package com.thepurpleweb.purplelauncher.home

import com.thepurpleweb.purplelauncher.profile.Profile

/**
 * Single resolution point for profile -> layout. Fluid, Premium,
 * and Expressive fall back to Calm until their own layouts are
 * built in later phases — intentional, not a bug.
 */
object HomeLayoutFactory {

    fun forProfile(profile: Profile): HomeLayout {
        return when (profile) {
            Profile.Focus -> FocusHomeLayout()
            else -> CalmHomeLayout()
        }
    }
}
