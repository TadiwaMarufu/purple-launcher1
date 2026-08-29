package com.thepurpleweb.purplelauncher.qs

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import com.thepurpleweb.purplelauncher.profile.ProfileEngine
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class ProfileTileService : TileService() {

    private var profileEngine: ProfileEngine? = null

    override fun onCreate() {
        super.onCreate()

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            try {
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))
                File(getExternalFilesDir(null), "tile_crash_log.txt")
                    .writeText(sw.toString())
            } catch (_: Exception) {
            }
        }

        profileEngine = ProfileEngine.getInstance(applicationContext)
    }

    override fun onStartListening() {
        super.onStartListening()
        Toast.makeText(this, "Tile listening started", Toast.LENGTH_SHORT).show()
        refreshTile()
    }

    override fun onClick() {
        super.onClick()

        Toast.makeText(this, "Tile tapped", Toast.LENGTH_SHORT).show()

        try {
            val engine = profileEngine
            if (engine == null) {
                Toast.makeText(this, "profileEngine is null", Toast.LENGTH_SHORT).show()
                return
            }

            engine.cycleNext()
            Toast.makeText(this, "Now: ${engine.current.value.displayName}", Toast.LENGTH_SHORT).show()
            refreshTile()

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun refreshTile() {
        val engine = profileEngine ?: return
        val current = engine.current.value

        qsTile?.apply {
            label = "Purple Launcher"
            subtitle = current.displayName
            state = Tile.STATE_ACTIVE
            updateTile()
        }
    }
}
