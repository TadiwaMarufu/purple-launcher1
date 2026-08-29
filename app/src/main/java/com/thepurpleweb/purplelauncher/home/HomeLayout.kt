package com.thepurpleweb.purplelauncher.home

import android.view.ViewGroup
import com.thepurpleweb.purplelauncher.apps.AppInfo

/**
 * Each profile provides its own implementation of this interface.
 * MainActivity only ever calls build() on whatever HomeLayout the
 * current profile resolves to — it never needs to know which
 * profile is active. This is the abstraction the spec calls for
 * instead of scattering `if (profile == X)` checks everywhere.
 */
interface HomeLayout {
    fun build(
        container: ViewGroup,
        apps: List<AppInfo>,
        onAppClick: (AppInfo) -> Unit
    )
}
