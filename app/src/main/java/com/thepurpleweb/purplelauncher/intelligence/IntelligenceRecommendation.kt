package com.thepurpleweb.purplelauncher.intelligence

/**
 * Explainable recommendation produced by Adaptive Intelligence.
 *
 * The engine recommends. The UI decides whether and how to present it.
 */
sealed class IntelligenceRecommendation {

    data object ReduceVisualEffects :
        IntelligenceRecommendation()

    data object EmphasizeProductivity :
        IntelligenceRecommendation()

    data object EmphasizeMedia :
        IntelligenceRecommendation()

    data object EmphasizeCommunication :
        IntelligenceRecommendation()

    data object EmphasizeNavigation :
        IntelligenceRecommendation()

    data object QuietPresentation :
        IntelligenceRecommendation()

    data object ShowCurrentProfile :
        IntelligenceRecommendation()
}
