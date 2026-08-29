package com.thepurpleweb.purplelauncher.profile

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.thepurpleweb.purplelauncher.R
import com.thepurpleweb.purplelauncher.home.HomeLayoutFactory

class ProfilesActivity : AppCompatActivity() {

    private lateinit var profileEngine: ProfileEngine
    private lateinit var listContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        profileEngine = ProfileEngine(applicationContext)

        buildUi()
    }

    override fun onResume() {
        super.onResume()

        if (::profileEngine.isInitialized && ::listContainer.isInitialized) {
            rebuildProfileList()
        }
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(18, 18, 18))
            setPadding(dp(20), dp(32), dp(20), dp(32))
        }

        val title = TextView(this).apply {
            text = "Profiles"
            textSize = 30f
            setTextColor(Color.WHITE)
        }

        val subtitle = TextView(this).apply {
            text = "Choose how Purple Launcher feels and behaves."
            textSize = 15f
            setTextColor(Color.rgb(165, 165, 165))
            setPadding(0, dp(6), 0, dp(24))
        }

        listContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        root.addView(title)
        root.addView(subtitle)
        root.addView(listContainer)

        val scroll = ScrollView(this).apply {
            addView(root)
        }

        setContentView(scroll)

        rebuildProfileList()
    }

    private fun rebuildProfileList() {
        listContainer.removeAllViews()

        val current = profileEngine.current.value

        Profile.all.forEach { profile ->
            val selected = profile.id == current.id

            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(18), dp(16), dp(18), dp(16))

                setBackgroundColor(
                    if (selected) {
                        Color.rgb(55, 38, 70)
                    } else {
                        Color.rgb(28, 28, 28)
                    }
                )

                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = dp(10)
                }

                isClickable = true
                isFocusable = true

                setOnClickListener {
                    profileEngine.setProfile(profile)
                    rebuildProfileList()
                }
            }

            val name = TextView(this).apply {
                text = if (selected) {
                    "${profile.displayName}  •  Active"
                } else {
                    profile.displayName
                }

                textSize = 18f
                setTextColor(Color.WHITE)
            }

            val description = TextView(this).apply {
                text = descriptionFor(profile)
                textSize = 13f
                setTextColor(Color.rgb(155, 155, 155))
                setPadding(0, dp(5), 0, 0)
            }

            card.addView(name)
            card.addView(description)

            listContainer.addView(card)
        }
    }

    private fun descriptionFor(profile: Profile): String {
        return when (profile) {
            Profile.Fluid ->
                "Smooth, flexible everyday launcher experience."

            Profile.Premium ->
                "Refined, polished experience with a richer visual feel."

            Profile.Calm ->
                "Minimal, quiet and distraction-free."

            Profile.Focus ->
                "Productivity-oriented layout designed for fast navigation."

            Profile.Expressive ->
                "More visual personality, motion and customization."
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
