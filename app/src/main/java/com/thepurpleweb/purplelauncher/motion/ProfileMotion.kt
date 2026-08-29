package com.thepurpleweb.purplelauncher.motion

import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import com.thepurpleweb.purplelauncher.profile.Profile
import com.thepurpleweb.purplelauncher.profile.ProfileDesign

data class ProfileMotion(
    val style: ProfileDesign.MotionStyle,
    val durationMs: Long,
    val translationDp: Float,
    val scaleFrom: Float,
    val alphaFrom: Float
) {
    companion object {
        fun forProfile(profile: Profile): ProfileMotion {
            return when (profile) {
                Profile.Fluid -> ProfileMotion(
                    ProfileDesign.MotionStyle.ORGANIC,
                    420L, 18f, 0.94f, 0f
                )

                Profile.Premium -> ProfileMotion(
                    ProfileDesign.MotionStyle.PRECISE,
                    260L, 8f, 0.98f, 0f
                )

                Profile.Calm -> ProfileMotion(
                    ProfileDesign.MotionStyle.RESTRAINED,
                    180L, 3f, 0.995f, 0f
                )

                Profile.Focus -> ProfileMotion(
                    ProfileDesign.MotionStyle.FAST,
                    140L, 6f, 0.99f, 0f
                )

                Profile.Expressive -> ProfileMotion(
                    ProfileDesign.MotionStyle.PLAYFUL,
                    480L, 24f, 0.90f, 0f
                )
            }
        }
    }
}

object MotionEngine {

    fun animateEntrance(
        view: android.view.View,
        motion: ProfileMotion,
        quality: com.thepurpleweb.purplelauncher.performance.VisualQuality
    ) {
        if (quality == com.thepurpleweb.purplelauncher.performance.VisualQuality.LOW) {
            view.alpha = 1f
            view.translationY = 0f
            view.scaleX = 1f
            view.scaleY = 1f
            return
        }

        view.alpha = motion.alphaFrom
        view.translationY = dp(view, motion.translationDp)
        view.scaleX = motion.scaleFrom
        view.scaleY = motion.scaleFrom

        val animator = view.animate()
            .alpha(1f)
            .translationY(0f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(motion.durationMs)

        animator.interpolator = when (motion.style) {
            ProfileDesign.MotionStyle.ORGANIC ->
                OvershootInterpolator(0.7f)

            ProfileDesign.MotionStyle.PRECISE ->
                DecelerateInterpolator()

            ProfileDesign.MotionStyle.RESTRAINED ->
                DecelerateInterpolator()

            ProfileDesign.MotionStyle.FAST ->
                LinearInterpolator()

            ProfileDesign.MotionStyle.PLAYFUL ->
                OvershootInterpolator(1.0f)
        }

        animator.start()
    }

    private fun dp(view: android.view.View, value: Float): Float =
        value * view.resources.displayMetrics.density
}
