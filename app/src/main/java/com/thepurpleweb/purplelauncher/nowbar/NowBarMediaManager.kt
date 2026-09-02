package com.thepurpleweb.purplelauncher.nowbar

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import com.thepurpleweb.purplelauncher.notifications.PurpleNotificationListenerService

class NowBarMediaManager(
    private val context: Context,
    private val onMediaChanged: (NowBarItem?) -> Unit
) {
    private val sessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager
    private var activeController: MediaController? = null

    private val controllerCallback = object : MediaController.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackState?) {
            updateMediaState()
        }

        override fun onMetadataChanged(metadata: MediaMetadata?) {
            updateMediaState()
        }
    }

    private val sessionListener = MediaSessionManager.OnActiveSessionsChangedListener { controllers ->
        attachToActiveSession(controllers)
    }

    fun start() {
        try {
            // Must reference an actual, manifest-registered notification
            // listener service that the user has granted access to.
            // PurpleNotificationListenerService is the one declared in
            // AndroidManifest.xml — this was previously pointed at a
            // NotificationListenerBridge class that was never registered,
            // which caused getActiveSessions() to throw a SecurityException
            // on every call, silently swallowed below, so MUSIC never
            // populated regardless of what was playing.
            val componentName = ComponentName(context, PurpleNotificationListenerService::class.java)
            sessionManager?.addOnActiveSessionsChangedListener(sessionListener, componentName)
            val initial = sessionManager?.getActiveSessions(componentName)
            attachToActiveSession(initial)
        } catch (_: Exception) {
            onMediaChanged(null)
        }
    }

    fun stop() {
        try {
            sessionManager?.removeOnActiveSessionsChangedListener(sessionListener)
            activeController?.unregisterCallback(controllerCallback)
            activeController = null
        } catch (_: Exception) {}
    }

    private fun attachToActiveSession(controllers: List<MediaController>?) {
        activeController?.unregisterCallback(controllerCallback)
        activeController = controllers?.firstOrNull { controller ->
            val state = controller.playbackState?.state
            state == PlaybackState.STATE_PLAYING || state == PlaybackState.STATE_BUFFERING
        } ?: controllers?.firstOrNull()

        activeController?.registerCallback(controllerCallback)
        updateMediaState()
    }

    private fun updateMediaState() {
        val controller = activeController ?: run {
            onMediaChanged(null)
            return
        }

        val state = controller.playbackState
        val isPlaying = state?.state == PlaybackState.STATE_PLAYING
        if (!isPlaying) {
            onMediaChanged(null)
            return
        }

        val metadata = controller.metadata
        val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)?.ifEmpty { "Playing Media" } ?: "Playing Media"
        val artist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)?.ifEmpty { "Unknown Artist" } ?: "Unknown Artist"
        val duration = metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: 0L
        val position = state?.position ?: 0L

        val progressPercent = if (duration > 0) {
            ((position.toFloat() / duration.toFloat()) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }

        onMediaChanged(
            NowBarItem(
                type = NowBarType.MUSIC,
                title = title,
                subtitle = artist,
                progress = progressPercent,
                isPersistent = false
            )
        )
    }
}
