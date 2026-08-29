package com.thepurpleweb.purplelauncher.icons

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import android.widget.ImageView
import com.thepurpleweb.purplelauncher.profile.Profile

/**
 * Centralized profile-aware icon treatment.
 *
 * The launcher keeps Android's original application Drawable intact and
 * applies presentation treatment at the ImageView level. This preserves
 * compatibility with normal Android icons while allowing each profile
 * to have its own visual language.
 *
 * Profiles:
 * Fluid      -> soft, alive, slightly organic presentation
 * Premium    -> restrained, refined, precise
 * Calm       -> quiet, monochrome-friendly, low visual noise
 * Focus      -> compact, information-oriented
 * Expressive -> stronger visual character and saturation
 */
object IconTreatment {

    fun apply(
        imageView: ImageView,
        drawable: Drawable,
        profile: Profile
    ) {
        val icon = drawable.constantState
            ?.newDrawable()
            ?.mutate()
            ?: drawable

        imageView.background = null
        imageView.setPadding(0, 0, 0, 0)
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        imageView.alpha = 1f

        // Never permanently mutate the application's shared Drawable.
        imageView.colorFilter = null

        when (profile) {
            Profile.Fluid -> applyFluid(imageView, icon)
            Profile.Premium -> applyPremium(imageView, icon)
            Profile.Calm -> applyCalm(imageView, icon)
            Profile.Focus -> applyFocus(imageView, icon)
            Profile.Expressive -> applyExpressive(imageView, icon)
        }

        imageView.setImageDrawable(icon)
    }

    private fun applyFluid(
        imageView: ImageView,
        icon: Drawable
    ) {
        // Fluid uses a soft translucent surface behind the icon and
        // slightly more generous presentation.
        imageView.background = roundedSurface(
            Color.argb(34, 170, 120, 255),
            18f
        )

        imageView.setPadding(5, 5, 5, 5)
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.clipToOutline = true
        }
    }

    private fun applyPremium(
        imageView: ImageView,
        icon: Drawable
    ) {
        // Premium is restrained rather than flashy.
        // A very subtle surface gives icons a controlled visual boundary.
        imageView.background = roundedSurface(
            Color.argb(20, 255, 255, 255),
            14f
        )

        imageView.setPadding(4, 4, 4, 4)
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
    }

    private fun applyCalm(
        imageView: ImageView,
        icon: Drawable
    ) {
        // Calm intentionally reduces visual noise.
        // Desaturation keeps colorful icons from dominating the interface.
        val matrix = ColorMatrix()
        matrix.setSaturation(0.35f)

        imageView.colorFilter = ColorMatrixColorFilter(matrix)
        imageView.alpha = 0.94f
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
    }

    private fun applyFocus(
        imageView: ImageView,
        icon: Drawable
    ) {
        // Focus favors compact, efficient presentation.
        imageView.background = roundedSurface(
            Color.argb(18, 255, 255, 255),
            10f
        )

        imageView.setPadding(3, 3, 3, 3)
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
    }

    private fun applyExpressive(
        imageView: ImageView,
        icon: Drawable
    ) {
        // Expressive allows icons to retain stronger color and presence.
        val matrix = ColorMatrix()
        matrix.setSaturation(1.18f)

        imageView.colorFilter = ColorMatrixColorFilter(matrix)
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER

        imageView.background = roundedSurface(
            Color.argb(30, 150, 90, 255),
            22f
        )

        imageView.setPadding(4, 4, 4, 4)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.clipToOutline = true
        }
    }

    private fun roundedSurface(
        color: Int,
        radiusDp: Float
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = radiusDp
        }
    }
}
