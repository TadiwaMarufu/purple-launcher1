package com.thepurpleweb.purplelauncher.wallpaper

import android.app.WallpaperColors
import android.app.WallpaperManager
import android.content.Context
import android.os.Build

/**
 * Thin wrapper around the real Android wallpaper APIs.
 *
 * enableLiveWallpaper() sets FLAG_SHOW_WALLPAPER on the window — this
 * is how AOSP launchers show wallpaper: no manual bitmap loading, no
 * storage permission, works for both static and live wallpapers, and
 * the OS handles rendering efficiently.
 *
 * getDominantColors() uses WallpaperManager.getWallpaperColors() (API
 * 27+), the same real, permission-free API Android's own Material You
 * theming uses. Never fabricates colors — returns null if unavailable
 * (older API, no wallpaper set, or any failure) so callers can
 * gracefully fall back to their existing fixed palette.
 */
object WallpaperProvider {

    fun getDominantColors(context: Context): WallpaperColors? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) {
            return null
        }

        return try {
            val manager = WallpaperManager.getInstance(context)
            manager.getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
        } catch (_: Exception) {
            null
        }
    }
}
