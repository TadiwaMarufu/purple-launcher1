package com.thepurpleweb.purplelauncher.settings

import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.thepurpleweb.purplelauncher.R

class SettingsActivity : AppCompatActivity() {

    private lateinit var repository: SettingsRepository

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        repository =
            SettingsRepository(
                applicationContext
            )

        buildUi()
    }

    private fun buildUi() {

        val root =
            LinearLayout(this).apply {
                orientation =
                    LinearLayout.VERTICAL

                setBackgroundColor(
                    Color.rgb(18, 18, 18)
                )

                setPadding(
                    dp(20),
                    dp(32),
                    dp(20),
                    dp(32)
                )
            }

        val title =
            TextView(this).apply {
                text = "Settings"
                textSize = 30f
                setTextColor(Color.WHITE)
                setPadding(
                    0,
                    0,
                    0,
                    dp(8)
                )
            }

        val subtitle =
            TextView(this).apply {
                text =
                    "Make Android yours."
                textSize = 15f
                setTextColor(
                    Color.rgb(
                        170,
                        170,
                        170
                    )
                )

                setPadding(
                    0,
                    0,
                    0,
                    dp(28)
                )
            }

        root.addView(title)
        root.addView(subtitle)

        addSection(
            root,
            "HOME"
        )

        addSwitch(
            root,
            "App labels",
            "Show application names on the home screen.",
            repository.settings.value.showAppLabels
        ) {
            repository.setShowAppLabels(it)
        }

        addSwitch(
            root,
            "Dock labels",
            "Show application names beneath dock icons.",
            repository.settings.value.showDockLabels
        ) {
            repository.setShowDockLabels(it)
        }

        addSection(
            root,
            "BEHAVIOR"
        )

        addSwitch(
            root,
            "Vibration",
            "Use haptic feedback for supported interactions.",
            repository.settings.value.vibrationEnabled
        ) {
            repository.setVibrationEnabled(it)
        }

        addSwitch(
            root,
            "Animations",
            "Allow launcher animations and transitions.",
            repository.settings.value.animationsEnabled
        ) {
            repository.setAnimationsEnabled(it)
        }

        addSwitch(
            root,
            "Reduced motion",
            "Reduce expensive and unnecessary motion.",
            repository.settings.value.reducedMotion
        ) {
            repository.setReducedMotion(it)
        }

        addSwitch(
            root,
            "Performance mode",
            "Prioritize responsiveness and memory usage on slower devices.",
            repository.settings.value.performanceMode
        ) {
            repository.setPerformanceMode(it)
        }

        addSection(
            root,
            "INTELLIGENCE"
        )

        addSwitch(
            root,
            "Smart dock",
            "Allow the dock to make contextual suggestions.",
            repository.settings.value.smartDockEnabled
        ) {
            repository.setSmartDockEnabled(it)
        }

        addSection(
            root,
            "APP DRAWER"
        )

        addSwitch(
            root,
            "Drawer search",
            "Show search functionality inside the app drawer.",
            repository.settings.value.appDrawerSearchEnabled
        ) {
            repository.setAppDrawerSearchEnabled(it)
        }

        val scroll =
            ScrollView(this).apply {
                addView(root)
            }

        setContentView(scroll)
    }

    private fun addSection(
        parent: LinearLayout,
        title: String
    ) {

        val view =
            TextView(this).apply {
                text = title
                textSize = 12f
                setTextColor(
                    Color.rgb(
                        180,
                        120,
                        255
                    )
                )

                setPadding(
                    0,
                    dp(22),
                    0,
                    dp(10)
                )
            }

        parent.addView(view)
    }

    private fun addSwitch(
        parent: LinearLayout,
        title: String,
        description: String,
        checked: Boolean,
        onChanged: (Boolean) -> Unit
    ) {

        val container =
            LinearLayout(this).apply {
                orientation =
                    LinearLayout.HORIZONTAL

                gravity =
                    Gravity.CENTER_VERTICAL

                minimumHeight =
                    dp(68)

                layoutParams =
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
            }

        val textContainer =
            LinearLayout(this).apply {
                orientation =
                    LinearLayout.VERTICAL

                layoutParams =
                    LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                    )
            }

        val titleView =
            TextView(this).apply {
                text = title
                textSize = 16f
                setTextColor(Color.WHITE)
            }

        val descriptionView =
            TextView(this).apply {
                text = description
                textSize = 12f
                setTextColor(
                    Color.rgb(
                        145,
                        145,
                        145
                    )
                )
            }

        val toggle =
            Switch(this).apply {
                isChecked = checked

                setOnCheckedChangeListener { _, value ->
                    onChanged(value)
                }
            }

        textContainer.addView(titleView)
        textContainer.addView(descriptionView)

        container.addView(textContainer)
        container.addView(toggle)

        parent.addView(container)
    }

    private fun dp(
        value: Int
    ): Int {
        return (
            value *
                resources.displayMetrics.density
            ).toInt()
    }
}
