package com.exemple.fauxappel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer

class CallActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            NotificationHelper.ACTION_ANSWER -> {
                NotificationHelper.annulerNotification(context)
                demarrerMusique(context)
            }
            NotificationHelper.ACTION_DECLINE -> {
                NotificationHelper.annulerNotification(context)
            }
            NotificationHelper.ACTION_HANGUP -> {
                arreterMusique()
            }
        }
    }

    companion object {
        private var mediaPlayer: MediaPlayer? = null

        fun demarrerMusique(context: Context) {
            arreterMusique() // on stoppe une éventuelle lecture précédente

            // R.raw.appel correspond au fichier appel.mp3 que tu dois placer
            // dans app/src/main/res/raw/appel.mp3
            mediaPlayer = MediaPlayer.create(context, R.raw.appel)
            mediaPlayer?.setOnCompletionListener {
                arreterMusique()
            }
            mediaPlayer?.start()
        }

        fun arreterMusique() {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
            mediaPlayer = null
        }
    }
}
