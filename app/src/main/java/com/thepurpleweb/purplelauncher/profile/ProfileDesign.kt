package com.thepurpleweb.purplelauncher.profile

data class ProfileDesign(
    val profile: Profile,
    val informationDensity: InformationDensity,
    val visualIntensity: VisualIntensity,
    val surfaceStyle: SurfaceStyle,
    val titleSizeSp: Float,
    val bodySizeSp: Float,
    val labelSizeSp: Float,
    val horizontalPaddingDp: Int,
    val verticalPaddingDp: Int,
    val itemSpacingDp: Int,
    val cornerRadiusDp: Int,
    val iconSizeDp: Int,
    val showAppLabels: Boolean,
    val motionStyle: MotionStyle,
    val motionScale: Float,
    val allowBlur: Boolean,
    val allowParallax: Boolean,
    val allowContinuousAnimation: Boolean
) {
    enum class InformationDensity {
        LOW, BALANCED, HIGH
    }

    enum class VisualIntensity {
        SUBDUED, BALANCED, VIVID
    }

    enum class SurfaceStyle {
        FLAT, SOFT, REFINED, FUNCTIONAL, EXPERIMENTAL
    }

    enum class MotionStyle {
        RESTRAINED, PRECISE, ORGANIC, FAST, PLAYFUL
    }

    companion object {
        fun forProfile(profile: Profile): ProfileDesign {
            return when (profile) {
                Profile.Fluid -> ProfileDesign(
                    profile,
                    InformationDensity.BALANCED,
                    VisualIntensity.VIVID,
                    SurfaceStyle.SOFT,
                    32f, 16f, 13f,
                    20, 20, 14, 24,
                    58, true,
                    MotionStyle.ORGANIC, 1.0f,
                    true, true, true
                )

                Profile.Premium -> ProfileDesign(
                    profile,
                    InformationDensity.BALANCED,
                    VisualIntensity.SUBDUED,
                    SurfaceStyle.REFINED,
                    30f, 15f, 12f,
                    24, 24, 16, 20,
                    56, true,
                    MotionStyle.PRECISE, 0.85f,
                    true, false, false
                )

                Profile.Calm -> ProfileDesign(
                    profile,
                    InformationDensity.LOW,
                    VisualIntensity.SUBDUED,
                    SurfaceStyle.FLAT,
                    28f, 15f, 12f,
                    28, 28, 20, 18,
                    54, true,
                    MotionStyle.RESTRAINED, 0.55f,
                    false, false, false
                )

                Profile.Focus -> ProfileDesign(
                    profile,
                    InformationDensity.HIGH,
                    VisualIntensity.BALANCED,
                    SurfaceStyle.FUNCTIONAL,
                    38f, 15f, 12f,
                    20, 16, 10, 12,
                    48, true,
                    MotionStyle.FAST, 0.65f,
                    false, false, false
                )

                Profile.Expressive -> ProfileDesign(
                    profile,
                    InformationDensity.BALANCED,
                    VisualIntensity.VIVID,
                    SurfaceStyle.EXPERIMENTAL,
                    42f, 17f, 13f,
                    16, 16, 18, 28,
                    64, false,
                    MotionStyle.PLAYFUL, 1.15f,
                    true, true, true
                )
            }
        }
    }
}
