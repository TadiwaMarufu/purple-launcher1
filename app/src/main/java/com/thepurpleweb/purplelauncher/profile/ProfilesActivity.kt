package com.thepurpleweb.purplelauncher.profile

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.thepurpleweb.purplelauncher.MainActivity
import com.thepurpleweb.purplelauncher.R

class ProfilesActivity : AppCompatActivity() {

    private lateinit var profileEngine: ProfileEngine
    private lateinit var listContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        profileEngine = ProfileEngine.getInstance(applicationContext)

        buildUi()
    }

    override fun onResume() {
        super.onResume()

        if (::profileEngine.isInitialized && ::listContainer.isInitialized) {
            renderProfiles()
        }
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(18, 18, 18))
            setPadding(
                dp(20),
                dp(32),
                dp(20),
                dp(32)
            )
        }

        val title = TextView(this).apply {
            text = "Profiles"
            textSize = 30f
            setTextColor(Color.WHITE)
        }

        val subtitle = TextView(this).apply {
            text = "Choose how Purple Launcher feels."
            textSize = 15f
            setTextColor(Color.rgb(170, 170, 170))
            setPadding(0, dp(8), 0, dp(28))
        }

        listContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        root.addView(title)
        root.addView(subtitle)
        root.addView(listContainer)

        val scrollView = ScrollView(this).apply {
            addView(root)
        }

        setContentView(scrollView)

        renderProfiles()
    }

    private fun renderProfiles() {
        listContainer.removeAllViews()

        val current = profileEngine.current.value

        Profile.all.forEach { profile ->

            val selected = profile.id == current.id

            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(
                    dp(18),
                    dp(16),
                    dp(18),
                    dp(16)
                )

                setBackgroundColor(
                    if (selected) {
                        Color.rgb(55, 35, 70)
                    } else {
                        Color.rgb(28, 28, 28)
                    }
                )

                isClickable = true
                isFocusable = true

                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(
                        0,
                        dp(6),
                        0,
                        dp(6)
                    )
                }

                setOnClickListener {
                    profileEngine.setProfile(profile)

                    // Immediately update the selector state.
                    renderProfiles()

                    // Return to the launcher so the newly selected
                    // profile is visible immediately.
                    setResult(RESULT_OK)
                    finish()
                }
            }

            val name = TextView(this).apply {
                text = if (selected) {
                    "${profile.displayName}  •  Active"
                } else {
                    profile.displayName
                }

                textSize = 18f
                setTextColor(
                    if (selected) {
                        Color.rgb(210, 170, 255)
                    } else {
                        Color.WHITE
                    }
                )
            }

            val description = TextView(this).apply {
                text = profileDescription(profile)
                textSize = 13f
                setTextColor(Color.rgb(150, 150, 150))
                setPadding(0, dp(5), 0, 0)
            }

            card.addView(name)
            card.addView(description)

            listContainer.addView(card)
        }
    }

    private fun profileDescription(profile: Profile): String {
        return when (profile) {
            Profile.Fluid ->
                "Smooth, adaptive and flowing."

            Profile.Premium ->
                "Polished, refined and expressive."

            Profile.Calm ->
                "Quiet, minimal and distraction-free."

            Profile.Focus ->
                "Structured for productivity and concentration."

            Profile.Expressive ->
                "Bold, energetic and highly visual."
        }
    }

    private fun dp(value: Int): Int {
        return (
            value * resources.displayMetrics.density
        ).toInt()
    }
}
