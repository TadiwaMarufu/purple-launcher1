package com.thepurpleweb.purplelauncher.icons

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.widget.ImageView
import com.thepurpleweb.purplelauncher.profile.Profile

enum class IconShape {
    CIRCLE,
    ROUNDED_SQUARE,
    SQUARE,
    ORIGINAL
}

/**
 * Centralized profile-aware icon treatment.
 *
 * Two layers of real, Android-compatible treatment on top of the
 * original per-profile surface/saturation styling:
 *
 * 1. Real monochrome extraction (API 33+): where the installed app
 *    actually ships an adaptive-icon monochrome layer, Calm uses it
 *    directly (tinted), rather than faking monochrome via desaturation.
 *    Falls back to the existing desaturation approach when unavailable
 *    (older API, or the app simply doesn't provide a monochrome layer)
 *    — a graceful degrade, not a fake, per the spec's "never fake
 *    system functionality" rule.
 *
 * 2. Real shape masking: icons are rendered into a bitmap and clipped
 *    to a per-profile path (circle / rounded square / square), rather
 *    than only drawing a colored surface behind an unclipped icon.
 *    Calm intentionally uses ORIGINAL (no reshape) to match its
 *    minimal-intervention personality.
 */
object IconTreatment {

    private const val RENDER_SIZE_PX = 144
    private val CALM_MONOCHROME_TINT = Color.rgb(200, 200, 205)

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
        imageView.colorFilter = null

        var monochromeApplied = false
        var rendered = icon

        if (profile == Profile.Calm) {
            val monochrome = tryExtractMonochrome(icon, CALM_MONOCHROME_TINT)
            if (monochrome != null) {
                rendered = monochrome
                monochromeApplied = true
            }
        }

        val shaped = maskToShape(rendered, shapeFor(profile), imageView.resources)

        when (profile) {
            Profile.Fluid -> applyFluid(imageView)
            Profile.Premium -> applyPremium(imageView)
            Profile.Calm -> applyCalm(imageView, monochromeApplied)
            Profile.Focus -> applyFocus(imageView)
            Profile.Expressive -> applyExpressive(imageView)
        }

        imageView.setImageDrawable(shaped)
    }

    private fun shapeFor(profile: Profile): IconShape = when (profile) {
        Profile.Fluid -> IconShape.CIRCLE
        Profile.Premium -> IconShape.ROUNDED_SQUARE
        Profile.Calm -> IconShape.ORIGINAL
        Profile.Focus -> IconShape.SQUARE
        Profile.Expressive -> IconShape.ROUNDED_SQUARE
    }

    /**
     * Only succeeds on API 33+ when the icon is a real AdaptiveIconDrawable
     * AND the app actually ships a monochrome layer. Returns null otherwise
     * so the caller can fall back — this never fabricates monochrome.
     */
    private fun tryExtractMonochrome(icon: Drawable, tintColor: Int): Drawable? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return null
        }

        val adaptive = icon as? AdaptiveIconDrawable ?: return null
        val monochromeLayer = adaptive.monochrome ?: return null

        return try {
            val tinted = monochromeLayer.mutate()
            tinted.setTint(tintColor)
            tinted.setTintMode(PorterDuff.Mode.SRC_IN)
            tinted
        } catch (_: Exception) {
            null
        }
    }

    private fun maskToShape(
        icon: Drawable,
        shape: IconShape,
        resources: Resources
    ): Drawable {
        if (shape == IconShape.ORIGINAL) {
            return icon
        }

        val size = RENDER_SIZE_PX

        val source = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val sourceCanvas = Canvas(source)
        icon.setBounds(0, 0, size, size)
        icon.draw(sourceCanvas)

        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val path = Path()
        when (shape) {
            IconShape.CIRCLE -> {
                val radius = size / 2f
                path.addCircle(radius, radius, radius, Path.Direction.CW)
            }

            IconShape.ROUNDED_SQUARE -> {
                val corner = size * 0.24f
                path.addRoundRect(
                    0f, 0f, size.toFloat(), size.toFloat(),
                    corner, corner,
                    Path.Direction.CW
                )
            }

            IconShape.SQUARE -> {
                path.addRect(0f, 0f, size.toFloat(), size.toFloat(), Path.Direction.CW)
            }

            IconShape.ORIGINAL -> {
                // Unreachable — handled above.
            }
        }

        // Fill the mask shape, then draw the source icon clipped to it
        // via SRC_IN — gives clean anti-aliased edges, unlike Canvas
        // .clipPath() which artifacts on hardware-accelerated canvases.
        canvas.drawPath(path, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(source, 0f, 0f, paint)

        return BitmapDrawable(resources, output)
    }

    private fun applyFluid(imageView: ImageView) {
        imageView.background = roundedSurface(Color.argb(34, 170, 120, 255), 18f)
        imageView.setPadding(5, 5, 5, 5)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.clipToOutline = true
        }
    }

    private fun applyPremium(imageView: ImageView) {
        imageView.background = roundedSurface(Color.argb(20, 255, 255, 255), 14f)
        imageView.setPadding(4, 4, 4, 4)
    }

    private fun applyCalm(imageView: ImageView, monochromeApplied: Boolean) {
        if (!monochromeApplied) {
            // Fallback: no real monochrome layer available on this
            // device/app, approximate with desaturation instead.
            val matrix = ColorMatrix()
            matrix.setSaturation(0.35f)
            imageView.colorFilter = ColorMatrixColorFilter(matrix)
        }
        imageView.alpha = 0.94f
    }

    private fun applyFocus(imageView: ImageView) {
        imageView.background = roundedSurface(Color.argb(18, 255, 255, 255), 10f)
        imageView.setPadding(3, 3, 3, 3)
    }

    private fun applyExpressive(imageView: ImageView) {
        val matrix = ColorMatrix()
        matrix.setSaturation(1.18f)
        imageView.colorFilter = ColorMatrixColorFilter(matrix)

        imageView.background = roundedSurface(Color.argb(30, 150, 90, 255), 22f)
        imageView.setPadding(4, 4, 4, 4)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.clipToOutline = true
        }
    }

    private fun roundedSurface(color: Int, radiusDp: Float): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = radiusDp
        }
    }
}
