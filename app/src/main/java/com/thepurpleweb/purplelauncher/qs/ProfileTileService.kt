package com.thepurpleweb.purplelauncher.qs

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.thepurpleweb.purplelauncher.profile.ProfileEngine

class ProfileTileService : TileService() {

    private var profileEngine: ProfileEngine? = null

    override fun onCreate() {
        super.onCreate()
        profileEngine = ProfileEngine(applicationContext)
    }

    override fun onStartListening() {
        super.onStartListening()
        refreshTile()
    }

    override fun onClick() {
        super.onClick()
        profileEngine?.cycleNext()
        refreshTile()
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
