package com.thepurpleweb.purplelauncher.canvas

import android.content.Context
import com.thepurpleweb.purplelauncher.profile.Profile

class CanvasRepository(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(
        "purple_canvas",
        Context.MODE_PRIVATE
    )

    fun getModules(profile: Profile): List<CanvasModuleState> {
        val raw = prefs.getString(keyFor(profile), null) ?: return defaultModulesFor()

        val parsed = raw.split(";")
            .filter { it.isNotBlank() }
            .mapNotNull { entry -> parseEntry(entry) }

        return parsed.ifEmpty { defaultModulesFor() }
    }

    fun saveModules(profile: Profile, modules: List<CanvasModuleState>) {
        val raw = modules.joinToString(";") { m ->
            "${m.id}|${m.type.name}|${m.xDp}|${m.yDp}|${m.widthDp}|${m.heightDp}"
        }
        prefs.edit().putString(keyFor(profile), raw).apply()
    }

    private fun parseEntry(entry: String): CanvasModuleState? {
        val parts = entry.split("|")
        if (parts.size != 6) return null

        return try {
            CanvasModuleState(
                id = parts[0],
                type = CanvasModuleType.valueOf(parts[1]),
                xDp = parts[2].toFloat(),
                yDp = parts[3].toFloat(),
                widthDp = parts[4].toFloat(),
                heightDp = parts[5].toFloat()
            )
        } catch (_: Exception) {
            null
        }
    }

    // No picker exists yet to add modules manually — so a fresh profile
    // starts with one of each type stacked vertically, which is the only
    // way to actually test Battery/Media/Quote until that's built.
    private fun defaultModulesFor(): List<CanvasModuleState> {
        return listOf(
            CanvasModuleState(
                id = "default_clock",
                type = CanvasModuleType.CLOCK,
                xDp = 24f,
                yDp = 24f,
                widthDp = 180f,
                heightDp = 90f
            ),
            CanvasModuleState(
                id = "default_battery",
                type = CanvasModuleType.BATTERY,
                xDp = 24f,
                yDp = 130f,
                widthDp = 160f,
                heightDp = 110f
            ),
            CanvasModuleState(
                id = "default_media",
                type = CanvasModuleType.MEDIA,
                xDp = 24f,
                yDp = 260f,
                widthDp = 220f,
                heightDp = 80f
            ),
            CanvasModuleState(
                id = "default_quote",
                type = CanvasModuleType.QUOTE,
                xDp = 24f,
                yDp = 360f,
                widthDp = 240f,
                heightDp = 100f
            )
        )
    }

    private fun keyFor(profile: Profile): String = "canvas_${profile.id}"
}
