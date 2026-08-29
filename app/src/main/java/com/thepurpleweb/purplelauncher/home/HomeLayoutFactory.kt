package com.thepurpleweb.purplelauncher.home

import com.thepurpleweb.purplelauncher.profile.Profile

object HomeLayoutFactory {

    fun forProfile(profile: Profile): HomeLayout {
        return when (profile) {
            Profile.Fluid -> FluidHomeLayout()
            Profile.Premium -> PremiumHomeLayout()
            Profile.Calm -> CalmHomeLayout()
            Profile.Focus -> FocusHomeLayout()
            Profile.Expressive -> ExpressiveHomeLayout()
        }
    }
}
