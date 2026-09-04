package com.thepurpleweb.purplelauncher.intelligence

import android.content.Context
import com.thepurpleweb.purplelauncher.profile.Profile

/**
 * Main entry point for launcher Adaptive Intelligence.
 */
class IntelligenceManager(
    context: Context
) {

    private val applicationContext =
        context.applicationContext

    private val preferences =
        IntelligencePreferences(
            applicationContext
        )

    private val contextProvider =
        LauncherContextProvider(
            applicationContext
        )

    private val engine =
        AdaptiveIntelligence(
            preferences
        )

    /**
     * isMediaPlaying must be supplied by the caller — LauncherContextProvider
     * has no visibility into media session state (that lives in the Now Bar
     * subsystem), so without this parameter it silently defaulted to false
     * forever, meaning EmphasizeMedia could never actually fire.
     */
    fun evaluate(
        profile: Profile,
        isMediaPlaying: Boolean = false
    ): List<IntelligenceRecommendation> {

        val context =
            contextProvider.snapshot().copy(
                isMediaPlaying = isMediaPlaying
            )

        return engine.evaluate(
            context,
            profile
        )
    }

    fun preferences():
        IntelligencePreferences {
        return preferences
    }

    fun currentContext():
        LauncherContext {
        return contextProvider.snapshot()
    }
}
