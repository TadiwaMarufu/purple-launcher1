package com.thepurpleweb.purplelauncher.intelligence

import com.thepurpleweb.purplelauncher.profile.Profile

/**
 * Local-first deterministic Adaptive Intelligence engine.
 *
 * This is deliberately NOT an AI/LLM system.
 *
 * Inputs:
 * - current profile
 * - time
 * - battery
 * - media state
 * - enabled intelligence signals
 *
 * Output:
 * - explainable recommendations
 *
 * The engine never directly modifies launcher UI.
 */
class AdaptiveIntelligence(
    private val preferences: IntelligencePreferences
) {

    fun evaluate(
        context: LauncherContext,
        profile: Profile
    ): List<IntelligenceRecommendation> {

        val recommendations =
            mutableListOf<IntelligenceRecommendation>()

        if (
            preferences.isEnabled(
                IntelligenceSignal.BATTERY
            ) &&
            context.batteryCritical
        ) {
            recommendations +=
                IntelligenceRecommendation.ReduceVisualEffects
        }

        if (
            preferences.isEnabled(
                IntelligenceSignal.TIME_PATTERN
            )
        ) {

            when {
                context.isMorning -> {
                    recommendations +=
                        IntelligenceRecommendation.EmphasizeNavigation
                }

                context.isEvening &&
                    preferences.isEnabled(
                        IntelligenceSignal.MEDIA
                    ) &&
                    context.isMediaPlaying -> {

                    recommendations +=
                        IntelligenceRecommendation.EmphasizeMedia
                }

                context.isNight -> {
                    recommendations +=
                        IntelligenceRecommendation.QuietPresentation
                }
            }
        }

        when (profile) {

            Profile.Focus -> {
                recommendations +=
                    IntelligenceRecommendation.EmphasizeProductivity
            }

            Profile.Calm -> {
                recommendations +=
                    IntelligenceRecommendation.QuietPresentation
            }

            Profile.Fluid -> {
                if (
                    preferences.isEnabled(
                        IntelligenceSignal.MEDIA
                    ) &&
                    context.isMediaPlaying
                ) {
                    recommendations +=
                        IntelligenceRecommendation.EmphasizeMedia
                }
            }

            Profile.Premium -> {
                recommendations +=
                    IntelligenceRecommendation.ShowCurrentProfile
            }

            Profile.Expressive -> {
                recommendations +=
                    IntelligenceRecommendation.ShowCurrentProfile
            }
        }

        return recommendations.distinct()
    }
}
