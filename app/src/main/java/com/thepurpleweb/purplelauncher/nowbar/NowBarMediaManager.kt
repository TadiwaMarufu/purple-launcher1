package com.thepurpleweb.purplelauncher.nowbar

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import com.thepurpleweb.purplelauncher.notifications.PurpleNotificationListenerService

class NowBarMediaManager(
    context: Context,
    private val onMediaChanged: (NowBarItem?) -> Unit
) {

    private val appContext =
        context.applicationContext

    private val mediaSessionManager =
        appContext.getSystemService(
            Context.MEDIA_SESSION_SERVICE
        ) as? MediaSessionManager

    private val notificationListenerComponent =
        ComponentName(
            appContext,
            PurpleNotificationListenerService::class.java
        )

    private var currentController:
        MediaController? = null

    private var started =
        false

    private val listener =
        MediaSessionManager.OnActiveSessionsChangedListener { controllers ->

            attachToBestController(
                controllers
            )
        }

    private val callback =
        object : MediaController.Callback() {

            override fun onMetadataChanged(
                metadata: MediaMetadata?
            ) {
                publish(
                    currentController
                )
            }

            override fun onPlaybackStateChanged(
                state: PlaybackState?
            ) {
                publish(
                    currentController
                )
            }

            override fun onSessionDestroyed() {

                detachCurrentController()

                refresh()
            }
        }

    fun start() {

        if (started) {
            refresh()
            return
        }

        val manager =
            mediaSessionManager

        if (manager == null) {

            onMediaChanged(null)

            return
        }

        if (
            !NowBarMediaAccess
                .isNotificationListenerEnabled(
                    appContext
                )
        ) {

            onMediaChanged(null)

            return
        }

        try {

            manager.addOnActiveSessionsChangedListener(
                listener,
                notificationListenerComponent
            )

            started =
                true

            refresh()

        } catch (
            _: SecurityException
        ) {

            started =
                false

            onMediaChanged(null)

        } catch (
            _: Exception
        ) {

            started =
                false

            onMediaChanged(null)
        }
    }

    fun stop() {

        val manager =
            mediaSessionManager

        if (manager != null && started) {

            try {

                manager.removeOnActiveSessionsChangedListener(
                    listener
                )

            } catch (
                _: Exception
            ) {
                // Ignore cleanup failures.
            }
        }

        started =
            false

        detachCurrentController()
    }

    fun refresh() {

        val manager =
            mediaSessionManager

        if (manager == null) {

            onMediaChanged(null)

            return
        }

        if (
            !NowBarMediaAccess
                .isNotificationListenerEnabled(
                    appContext
                )
        ) {

            detachCurrentController()

            onMediaChanged(null)

            return
        }

        try {

            val controllers =
                manager.getActiveSessions(
                    notificationListenerComponent
                )

            attachToBestController(
                controllers
            )

        } catch (
            _: SecurityException
        ) {

            detachCurrentController()

            onMediaChanged(null)

        } catch (
            _: Exception
        ) {

            detachCurrentController()

            onMediaChanged(null)
        }
    }

    private fun attachToBestController(
        controllers: List<MediaController>?
    ) {

        val best =
            controllers
                ?.firstOrNull { controller ->

                    val state =
                        controller.playbackState

                    state != null &&
                        (
                            state.state ==
                                PlaybackState.STATE_PLAYING ||
                            state.state ==
                                PlaybackState.STATE_PAUSED
                        )
                }

        if (
            best?.sessionToken ==
            currentController?.sessionToken
        ) {

            publish(
                currentController
            )

            return
        }

        detachCurrentController()

        currentController =
            best

        currentController?.registerCallback(
            callback
        )

        publish(
            currentController
        )
    }

    private fun detachCurrentController() {

        currentController?.let { controller ->

            try {

                controller.unregisterCallback(
                    callback
                )

            } catch (
                _: Exception
            ) {
                // Ignore cleanup failures.
            }
        }

        currentController =
            null
    }

    private fun publish(
        controller: MediaController?
    ) {

        if (controller == null) {

            onMediaChanged(null)

            return
        }

        val state =
            controller.playbackState

        if (
            state == null ||
            (
                state.state !=
                    PlaybackState.STATE_PLAYING &&
                state.state !=
                    PlaybackState.STATE_PAUSED
            )
        ) {

            onMediaChanged(null)

            return
        }

        val metadata =
            controller.metadata

        val title =
            metadata
                ?.getString(
                    MediaMetadata.METADATA_KEY_TITLE
                )
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: "Now playing"

        val artist =
            metadata
                ?.getString(
                    MediaMetadata.METADATA_KEY_ARTIST
                )
                ?.takeIf {
                    it.isNotBlank()
                }

        val isPlaying =
            state.state ==
                PlaybackState.STATE_PLAYING

        val subtitle =
            if (!artist.isNullOrBlank()) {

                if (isPlaying) {
                    artist
                } else {
                    "$artist • Paused"
                }

            } else {

                if (isPlaying) {
                    "Playing"
                } else {
                    "Paused"
                }
            }

        onMediaChanged(
            NowBarItem(
                type =
                    NowBarType.MUSIC,

                title =
                    title,

                subtitle =
                    subtitle,

                isPersistent =
                    true
            )
        )
    }
}
