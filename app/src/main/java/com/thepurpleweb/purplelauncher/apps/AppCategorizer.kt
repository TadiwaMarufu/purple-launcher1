package com.thepurpleweb.purplelauncher.apps

import android.content.pm.ApplicationInfo

class AppCategorizer {

    fun categorize(app: AppInfo): AppCategory {
        val packageName = app.packageName.lowercase()
        val label = app.label.lowercase()

        return when {
            containsAny(
                packageName,
                label,
                "dialer", "contacts", "messaging", "message",
                "sms", "mms", "phone", "mail", "email",
                "telegram", "whatsapp", "signal", "messenger"
            ) -> AppCategory.COMMUNICATION

            containsAny(
                packageName,
                label,
                "facebook", "instagram", "twitter", "tiktok",
                "snapchat", "reddit", "discord", "social"
            ) -> AppCategory.SOCIAL

            containsAny(
                packageName,
                label,
                "youtube", "spotify", "music", "video",
                "netflix", "gallery", "photos", "camera",
                "player", "media"
            ) -> AppCategory.MEDIA

            containsAny(
                packageName,
                label,
                "office", "docs", "sheets", "drive",
                "calendar", "slack", "teams", "zoom",
                "notion", "work", "word", "excel"
            ) -> AppCategory.WORK

            containsAny(
                packageName,
                label,
                "game", "games", "playgames", "minecraft",
                "roblox", "pubg", "codm", "freefire"
            ) -> AppCategory.GAMES

            containsAny(
                packageName,
                label,
                "settings", "calculator", "clock", "weather",
                "files", "filemanager", "terminal", "browser",
                "chrome", "firefox", "security", "tool",
                "tools", "manager"
            ) -> AppCategory.TOOLS

            else -> AppCategory.OTHER
        }
    }

    private fun containsAny(
        packageName: String,
        label: String,
        vararg values: String
    ): Boolean {
        return values.any {
            packageName.contains(it) || label.contains(it)
        }
    }
}
