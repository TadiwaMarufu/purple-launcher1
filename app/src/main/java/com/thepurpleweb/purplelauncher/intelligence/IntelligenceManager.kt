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

    fun evaluate(
        profile: Profile
    ): List<IntelligenceRecommendation> {

        val context =
            contextProvider.snapshot()

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
