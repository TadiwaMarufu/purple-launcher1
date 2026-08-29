package com.thepurpleweb.purplelauncher.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView

enum class MotionStyle {
    FLUID,
    PREMIUM,
    CALM,
    FOCUS,
    EXPRESSIVE
}

data class ProfileVisuals(
    val motion: MotionStyle,
    val background: Int,
    val primaryText: Int,
    val secondaryText: Int,
    val accent: Int,
    val card: Int,
    val cornerRadiusDp: Float,
    val titleSizeSp: Float,
    val bodySizeSp: Float,
    val horizontalPaddingDp: Int
)

object ProfileVisualsProvider {

    fun forMotion(style: MotionStyle): ProfileVisuals {
        return when (style) {
            MotionStyle.FLUID -> ProfileVisuals(
                motion = MotionStyle.FLUID,
                background = Color.rgb(14, 10, 22),
                primaryText = Color.rgb(248, 244, 255),
                secondaryText = Color.rgb(180, 166, 196),
                accent = Color.rgb(190, 105, 255),
                card = Color.rgb(31, 22, 43),
                cornerRadiusDp = 26f,
                titleSizeSp = 34f,
                bodySizeSp = 15f,
                horizontalPaddingDp = 18
            )

            MotionStyle.PREMIUM -> ProfileVisuals(
                motion = MotionStyle.PREMIUM,
                background = Color.rgb(12, 12, 15),
                primaryText = Color.rgb(245, 243, 248),
                secondaryText = Color.rgb(163, 160, 170),
                accent = Color.rgb(205, 177, 255),
                card = Color.rgb(27, 26, 31),
                cornerRadiusDp = 18f,
                titleSizeSp = 32f,
                bodySizeSp = 14f,
                horizontalPaddingDp = 22
            )

            MotionStyle.CALM -> ProfileVisuals(
                motion = MotionStyle.CALM,
                background = Color.rgb(16, 16, 18),
                primaryText = Color.rgb(238, 238, 242),
                secondaryText = Color.rgb(145, 145, 152),
                accent = Color.rgb(170, 125, 210),
                card = Color.rgb(25, 25, 28),
                cornerRadiusDp = 14f,
                titleSizeSp = 30f,
                bodySizeSp = 14f,
                horizontalPaddingDp = 20
            )

            MotionStyle.FOCUS -> ProfileVisuals(
                motion = MotionStyle.FOCUS,
                background = Color.rgb(11, 13, 17),
                primaryText = Color.rgb(244, 246, 250),
                secondaryText = Color.rgb(145, 151, 163),
                accent = Color.rgb(169, 116, 255),
                card = Color.rgb(23, 26, 33),
                cornerRadiusDp = 12f,
                titleSizeSp = 38f,
                bodySizeSp = 14f,
                horizontalPaddingDp = 20
            )

            MotionStyle.EXPRESSIVE -> ProfileVisuals(
                motion = MotionStyle.EXPRESSIVE,
                background = Color.rgb(19, 10, 25),
                primaryText = Color.rgb(255, 246, 255),
                secondaryText = Color.rgb(190, 158, 201),
                accent = Color.rgb(221, 91, 255),
                card = Color.rgb(39, 19, 48),
                cornerRadiusDp = 30f,
                titleSizeSp = 42f,
                bodySizeSp = 16f,
                horizontalPaddingDp = 16
            )
        }
    }

    fun dp(view: View, value: Int): Int {
        return (value * view.resources.displayMetrics.density).toInt()
    }

    fun roundedBackground(
        view: View,
        color: Int,
        radiusDp: Float
    ) {
        val radius = radiusDp * view.resources.displayMetrics.density
        view.background = GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius
        }
    }

    fun animate(
        root: View,
        style: MotionStyle
    ) {
        root.alpha = 0f

        when (style) {
            MotionStyle.FLUID -> {
                root.translationY = dp(root, 18).toFloat()
                root.scaleX = 0.97f
                root.scaleY = 0.97f

                AnimatorSet().apply {
                    playTogether(
                        ObjectAnimator.ofFloat(root, View.ALPHA, 0f, 1f),
                        ObjectAnimator.ofFloat(root, View.TRANSLATION_Y, root.translationY, 0f),
                        ObjectAnimator.ofFloat(root, View.SCALE_X, 0.97f, 1f),
                        ObjectAnimator.ofFloat(root, View.SCALE_Y, 0.97f, 1f)
                    )
                    duration = 650
                    interpolator = OvershootInterpolator(0.8f)
                    start()
                }
            }

            MotionStyle.PREMIUM -> {
                root.translationY = dp(root, 8).toFloat()

                AnimatorSet().apply {
                    playTogether(
                        ObjectAnimator.ofFloat(root, View.ALPHA, 0f, 1f),
                        ObjectAnimator.ofFloat(root, View.TRANSLATION_Y, root.translationY, 0f)
                    )
                    duration = 420
                    interpolator = DecelerateInterpolator(1.5f)
                    start()
                }
            }

            MotionStyle.CALM -> {
                AnimatorSet().apply {
                    playTogether(
                        ObjectAnimator.ofFloat(root, View.ALPHA, 0f, 1f)
                    )
                    duration = 280
                    interpolator = DecelerateInterpolator()
                    start()
                }
            }

            MotionStyle.FOCUS -> {
                root.translationX = dp(root, 20).toFloat()

                AnimatorSet().apply {
                    playTogether(
                        ObjectAnimator.ofFloat(root, View.ALPHA, 0f, 1f),
                        ObjectAnimator.ofFloat(root, View.TRANSLATION_X, root.translationX, 0f)
                    )
                    duration = 220
                    interpolator = DecelerateInterpolator()
                    start()
                }
            }

            MotionStyle.EXPRESSIVE -> {
                root.translationY = dp(root, 30).toFloat()
                root.rotation = -2f
                root.scaleX = 0.94f
                root.scaleY = 0.94f

                AnimatorSet().apply {
                    playTogether(
                        ObjectAnimator.ofFloat(root, View.ALPHA, 0f, 1f),
                        ObjectAnimator.ofFloat(root, View.TRANSLATION_Y, root.translationY, 0f),
                        ObjectAnimator.ofFloat(root, View.ROTATION, -2f, 0f),
                        ObjectAnimator.ofFloat(root, View.SCALE_X, 0.94f, 1f),
                        ObjectAnimator.ofFloat(root, View.SCALE_Y, 0.94f, 1f)
                    )
                    duration = 700
                    interpolator = OvershootInterpolator(1.2f)
                    start()
                }
            }
        }
    }

    fun animateChildren(
        container: ViewGroup,
        style: MotionStyle
    ) {
        val count = container.childCount

        for (i in 0 until count) {
            val child = container.getChildAt(i)

            child.alpha = 0f

            when (style) {
                MotionStyle.FLUID -> {
                    child.translationY = dp(child, 20).toFloat()
                    child.scaleX = 0.96f
                    child.scaleY = 0.96f

                    child.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setStartDelay((i * 35L).coerceAtMost(280L))
                        .setDuration(520)
                        .setInterpolator(OvershootInterpolator(0.7f))
                        .start()
                }

                MotionStyle.PREMIUM -> {
                    child.translationY = dp(child, 8).toFloat()

                    child.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setStartDelay((i * 22L).coerceAtMost(160L))
                        .setDuration(350)
                        .setInterpolator(DecelerateInterpolator(1.5f))
                        .start()
                }

                MotionStyle.CALM -> {
                    child.animate()
                        .alpha(1f)
                        .setStartDelay((i * 15L).coerceAtMost(100L))
                        .setDuration(250)
                        .start()
                }

                MotionStyle.FOCUS -> {
                    child.translationX = dp(child, 14).toFloat()

                    child.animate()
                        .alpha(1f)
                        .translationX(0f)
                        .setStartDelay((i * 18L).coerceAtMost(120L))
                        .setDuration(200)
                        .setInterpolator(DecelerateInterpolator())
                        .start()
                }

                MotionStyle.EXPRESSIVE -> {
                    child.translationY = dp(child, 24).toFloat()
                    child.rotation = if (i % 2 == 0) -1.5f else 1.5f
                    child.scaleX = 0.92f
                    child.scaleY = 0.92f

                    child.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .rotation(0f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setStartDelay((i * 40L).coerceAtMost(320L))
                        .setDuration(560)
                        .setInterpolator(OvershootInterpolator(1f))
                        .start()
                }
            }
        }
    }

    fun pulse(view: View, style: MotionStyle) {
        if (style == MotionStyle.CALM) {
            return
        }

        val scale = when (style) {
            MotionStyle.FLUID -> 1.035f
            MotionStyle.PREMIUM -> 1.015f
            MotionStyle.FOCUS -> 1.02f
            MotionStyle.EXPRESSIVE -> 1.06f
            MotionStyle.CALM -> 1f
        }

        view.animate()
            .scaleX(scale)
            .scaleY(scale)
            .setDuration(120)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(180)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }
            .start()
    }
}
