package com.radiopinocchio.app

import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class RadioService : MediaSessionService() {

    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null

    private val streamUrl = "https://urbanwolf.zapto.org/listen/radiourbanwolf/radio.mp3"

    override fun onCreate() {
        super.onCreate()

        // 1. Configurazione AudioAttributes per musica/media
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        // 2. Inizializzazione ExoPlayer
        player = ExoPlayer.Builder(this).build().apply {
            setAudioAttributes(audioAttributes, true)
            // Prepara subito lo stream di Radio Urban Wolf
            val mediaItem = MediaItem.fromUri(streamUrl)
            setMediaItem(mediaItem)
            prepare()
        }

        // 3. Creazione della MediaSession (gestisce automaticamente notifiche e background)
        player?.let {
            mediaSession = MediaSession.Builder(this, it).build()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}