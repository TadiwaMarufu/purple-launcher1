package com.thepurpleweb.purplelauncher.search

import java.util.regex.Pattern

class SearchCommandParser {

    fun parse(query: String): SearchCommand {
        val trimmed = query.trim().lowercase()

        when (trimmed) {
            "drawer", "app drawer", "apps", "applications" -> return SearchCommand.OpenAppDrawer
            "focus", "switch focus" -> return SearchCommand.SwitchProfile
            "settings", "launcher settings" -> return SearchCommand.OpenSettings
        }

        if (trimmed.startsWith("timer ") || trimmed.startsWith("timer")) {
            val parts = trimmed.split("\\s+".toRegex())
            if (parts.size >= 2) {
                val durationMs = parseDuration(parts[1])
                if (durationMs > 0) {
                    val label = if (parts.size > 2) parts.subList(2, parts.size).joinToString(" ") else "Timer"
                    return SearchCommand.StartTimer(durationMs, label)
                }
            }
        }

        if (trimmed.startsWith("nowbar ") || trimmed.startsWith("custom ")) {
            val msg = query.trim().substringAfter(" ").trim()
            if (msg.isNotEmpty()) {
                return SearchCommand.SetCustomNowBar(msg)
            }
        }

        return SearchCommand.None
    }

    private fun parseDuration(input: String): Long {
        val matcher = Pattern.compile("^(\\d+)([smh]?)$").matcher(input)
        if (matcher.matches()) {
            val amount = matcher.group(1)?.toLongOrNull() ?: return 0L
            val unit = matcher.group(2) ?: "m"
            return when (unit) {
                "s" -> amount * 1000L
                "h" -> amount * 3600 * 1000L
                else -> amount * 60 * 1000L // default to minutes
            }
        }
        return 0L
    }
}
